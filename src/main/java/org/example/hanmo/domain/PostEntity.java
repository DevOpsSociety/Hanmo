package org.example.hanmo.domain;

import jakarta.persistence.*;

import org.example.hanmo.dto.post.request.PostRequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEntity extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "post_id")
  private Long id;

  @Column(length = 70, nullable = false)
  private String content;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  public void update(PostRequestDto postRequestDto) {
    this.content = postRequestDto.getContent();
  }
}
