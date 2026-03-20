package selfit.selfit.domain.fitted.entity;

import jakarta.persistence.*;
import lombok.*;
import selfit.selfit.domain.user.entity.User;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "FittedImage")
public class FittedImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fitted_url", columnDefinition = "TEXT")
    private String fitted_url;

    private Date createDate;
    private Date updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public FittedImage(String fitted_url, User user) {
        this.fitted_url = fitted_url;
        this.user = user;
        this.createDate = new Date();
        this.updateDate = new Date();
    }
}
