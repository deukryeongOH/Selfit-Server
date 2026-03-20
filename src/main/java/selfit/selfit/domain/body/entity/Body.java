package selfit.selfit.domain.body.entity;

import jakarta.persistence.*;
import lombok.*;
import selfit.selfit.domain.body.dto.BodySizeDto;
import selfit.selfit.domain.user.entity.User;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "Body")
public class Body {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String height;
    private String weight;
    private String waist;
    private String leg;
    private String shoulder;
    private String pelvis;
    private String chest;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "full_body_photos",
            joinColumns = @JoinColumn(name = "body_id"))
    @Column(name = "full_body_path", nullable = false)
    private List<String> fullBodyPhotos = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "face_photos",
            joinColumns = @JoinColumn(name = "body_id"))
    @Column(name = "face_path", nullable = false)
    private List<String> facePhotos = new ArrayList<>();

    @Column(name="create_date", nullable=false)
    private Date create_date;

    @Column(name="update_date", nullable=false)
    private Date update_date;

    @Builder
    public Body(User user, String height, String weight, String waist, String leg, String shoulder, String pelvis, String chest) {
        this.user       = user;
        this.height       = height;
        this.weight       = weight;
        this.waist        = waist;
        this.leg        = leg;
        this.shoulder    = shoulder;
        this.pelvis     = pelvis;
        this.chest      = chest;
        this.create_date = new Date();
        this.update_date = new Date();
    }

}
