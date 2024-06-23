package ru.practicum.stat.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.service.model.Record;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
}
