package com.taskhub.project.core.auth.authorization;

import com.taskhub.project.core.auth.authorization.domain.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepo extends JpaRepository<Action, String> {
}
