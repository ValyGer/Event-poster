package ru.practicum.stat.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stat.service.dto.ApplicationDtoForHits;
import ru.practicum.stat.service.model.Record;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("select new ru.practicum.stat.service.dto.ApplicationDtoForHits(r.app.app, r.uri, count (distinct r.ip))" +
            "from Record r " +
            "where r.timestamp between ?1 and ?2 " +
            "group by r.app.app, r.uri " +
            "order by count (distinct r.ip) desc")
    List<ApplicationDtoForHits> findAllUniqueRecordsWhenUriIsEmpty(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stat.service.dto.ApplicationDtoForHits(r.app.app, r.uri, count (distinct r.ip)) "
            + "from Record r "
            + "where r.timestamp between ?1 and ?2 "
            + "and r.uri in (?3)"
            + "group by r.app.app, r.uri "
            + "order by count (distinct r.ip) desc ")
    List<ApplicationDtoForHits> findAllUniqueRecordsWhenUriIsNotEmpty(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stat.service.dto.ApplicationDtoForHits(r.app.app, r.uri, count (distinct r.ip))" +
            "from Record r " +
            "where r.timestamp between ?1 and ?2 " +
            "group by r.app.app, r.uri " +
            "order by count (r.ip) desc")
    List<ApplicationDtoForHits> findAllRecordsWhenUriIsEmpty(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stat.service.dto.ApplicationDtoForHits(r.app.app, r.uri, count (distinct r.ip)) "
            + "from Record r "
            + "where r.timestamp between ?1 and ?2 "
            + "and r.uri in (?3)"
            + "group by r.app.app, r.uri "
            + "order by count (distinct r.ip) desc ")
    List<ApplicationDtoForHits> findAllRecordsWhenStarEndUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
