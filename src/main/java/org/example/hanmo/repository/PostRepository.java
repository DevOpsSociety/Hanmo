package org.example.hanmo.repository;

import org.example.hanmo.domain.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findAllByOrderByCreateDateDesc(Pageable pageable);
}
