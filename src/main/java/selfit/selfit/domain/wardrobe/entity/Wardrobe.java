package selfit.selfit.domain.wardrobe.entity;

import jakarta.persistence.*;
import lombok.*;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.user.entity.User;

import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Setter
@Table(name = "Wardrobe")
public class Wardrobe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "create_date", nullable = false)
    private Date create_date;

    @Column(name = "update_date", nullable = false)
    private Date update_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "path", nullable = false)
    private String path;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ClothesType type;

    @Builder
    public Wardrobe(User user, String path, ClothesType type) {
        this.user = user;
        this.path = path;
        this.type = type;
        this.create_date = new Date();
        this.update_date = new Date();
    }
}
