package org.example.hanmo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.hanmo.dto.post.request.PostRequestDto;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "nmos")
@NoArgsConstructor
@AllArgsConstructor
public class NmoEntity extends BaseTimeEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "nmo_id")
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  @Column(nullable = false)
  private int recruitLimit; // 모집 인원

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity author;

  @OneToMany(mappedBy = "nmo", cascade = CascadeType.ALL, orphanRemoval = false)
  private List<NmoApplicationEntity> application = new ArrayList<>();

  public void update(String title, String content, int recruitLimit) {
    this.title = title;
    this.content = content;
    this.recruitLimit = recruitLimit;
  }


}
