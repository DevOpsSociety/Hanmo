package org.example.hanmo.repository.post;

import org.example.hanmo.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostEntity> getLatestPosts(Pageable pageable);
}
