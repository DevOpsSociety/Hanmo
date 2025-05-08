package org.example.hanmo.repository.post;

import org.example.hanmo.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
  Page<PostEntity> getLatestPosts(Pageable pageable);
  List<PostEntity> findOldPostsToDelete(long excessCount);
}
