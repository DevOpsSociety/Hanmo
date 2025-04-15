package org.example.hanmo.repository.post;

import org.example.hanmo.domain.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long>, PostRepositoryCustom {}
