package org.example.hanmo.repository.Nmo;

import org.example.hanmo.domain.NmoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NmoRepository extends JpaRepository<NmoEntity, Long>, NmoRepositoryCustom {
}
