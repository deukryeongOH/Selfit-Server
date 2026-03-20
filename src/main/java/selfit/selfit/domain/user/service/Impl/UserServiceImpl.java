package selfit.selfit.domain.user.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import selfit.selfit.domain.body.entity.Body;
import selfit.selfit.domain.body.repository.BodyRepository;
import selfit.selfit.domain.user.dto.UserAccountDto;
import selfit.selfit.domain.user.dto.UserDetailDto;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.user.repository.UserRepository;
import selfit.selfit.domain.user.service.UserService;
import selfit.selfit.domain.wardrobe.entity.Wardrobe;
import selfit.selfit.domain.wardrobe.repository.WardrobeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원 가입(계정 생성)
     */
    @Override
    public User registerUser(UserAccountDto userAccountDto) {
        if(userAccountDto == null){
            throw new IllegalStateException("회원가입 정보가 필요합니다.");
        }

        // 중복 계정 검사
        validateDuplicateUser(userAccountDto.getAccountId());

        User user = User.builder()
                .accountId(userAccountDto.getAccountId())
                .password(passwordEncoder.encode(userAccountDto.getPassword()))
                .email(userAccountDto.getEmail())
                .build();

        Body body = Body.builder()
                .user(user)
                .build();

        user.setBody(body);

        return userRepository.save(user);
    }

    // 중복 계정 검사
    private void validateDuplicateUser(String accountId) {
//        Optional<User> findUser = userRepository.findById(user.getId());
//        if(findUser.isPresent()){ // user와 같은 값의 Id를 가진 객체가 있으면
//            throw new IllegalStateException("이미 존재하는 계정입니다.");
//        }
        Optional<User> findUser = userRepository.findByAccountId(accountId);
        if(findUser.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 계정 ID입니다.");
        }
    }

    /**
     * 개인정보 등록
     */

    public User updateUserDetails(UserDetailDto userDetailDto, String accountId){
        if(userDetailDto == null){
            throw new IllegalArgumentException("개인정보 등록 정보가 필요합니다.");
        }

        if(validateDuplicateNickname(userDetailDto.getNickname())){
            throw new IllegalArgumentException("존재하는 닉네임 입니다.");
        }

        User user = findUser(accountId);

        user.setName(userDetailDto.getName());
        user.setAge(userDetailDto.getAge());
        user.setNickname(userDetailDto.getNickname());
        user.setGender(userDetailDto.getGender());
        user.setUpdate_date(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     *  닉네임 중복 확인
     */
    public boolean validateDuplicateNickname(String nickname){
        // 회원 전체를 돌면서 nickname 비교
        for(User findUser : findAllUsers()){
            String findNickname = findUser.getNickname() == null ? "" : findUser.getNickname();
            if(findNickname.equals(nickname)){
                return true;
            }
        }
        return false;
    }

    /**
     * 전체 회원 조회
     */
    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    /**
     * 회원 조회
     */
    public User findUser(String accountId){
        return userRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디를 가진 회원이 존재하지 않습니다."));
    }

    /**
     * 아이디 찾기(이메일)
     */
    public String findAccountId(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 존재하지 않습니다."));

        return user.getAccountId();
    }

    /**
     *  임시 비밀번호 발급 (일단 계정 아이디와 이메일로 인증 후 임시 비밀번호 제공)
     */

    public String recoverPassword(String accountId, String email){
        User user = findUser(accountId);

        if(user.getEmail().equals(email)){
            String tempPwd = RandomStringUtils.randomAlphabetic(10);
            user.setPassword(passwordEncoder.encode(tempPwd));
            userRepository.save(user);
            return tempPwd;
        }
        else{
            throw new IllegalArgumentException("이메일이 계정의 이메일과 일치하지 않습니다.");
        }
    }

    /**
     *  비밀번호 재설정
     */
    public void resetPassword(String accountId, String temporaryPassword, String newPassword, String newPwd){
        User user = findUser(accountId);

        if(user.getPassword().equals(temporaryPassword)){
            user.setPassword(passwordEncoder.encode(newPwd));
            userRepository.save(user);
        }
        else{
            throw new IllegalArgumentException("유효하지 않은 비밀번호 입니다.");
        }
    }

}
