package selfit.selfit.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import selfit.selfit.global.security.springsecurity.CustomUserDetailsService;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshTokenExpiration;

    private SecretKey key;
    private final CustomUserDetailsService customUserDetailsService;

    @PostConstruct
    public void init(){
        byte[] bytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createAccessToken(Authentication authentication){
        Date now = new Date();
        String accountId = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(accountId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+accessTokenExpiration))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken(Authentication authentication){
        Date now = new Date();
        String accountId = authentication.getName();

        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(accountId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(new Date(now.getTime()+refreshTokenExpiration))
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch(io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Authentication getAuthentication(String token){
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(claims.getSubject());

        List<?> roles = (List<?>)claims.get("roles");
        List<SimpleGrantedAuthority> auths = roles.stream().map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(userDetails, token, auths);
    }

    // 토큰 유효성 검증(Access, Refresh 동일하게 사용)

}
