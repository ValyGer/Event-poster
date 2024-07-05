package ru.practicum.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.ViewStats;
import ru.practicum.stats.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.stats.ViewStats(h.app, h.uri, count (distinct h.ip)) " +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count (distinct h.ip) desc")
    List<ViewStats> findAllUniqueHitsWhenUriIsEmpty(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.ViewStats(h.app, h.uri, count (distinct h.ip)) "
            + "from Hit h "
            + "where h.timestamp between ?1 and ?2 "
            + "and h.uri in (?3)"
            + "group by h.app, h.uri "
            + "order by count (distinct h.ip) desc ")
    List<ViewStats> findAllUniqueHitsWhenUriIsNotEmpty(
            LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stats.ViewStats(h.app, h.uri, count (h.ip))" +
            "from Hit h " +
            "where h.timestamp between ?1 and ?2 " +
            "group by h.app, h.uri " +
            "order by count (h.ip) desc")
    List<ViewStats> findAllHitsWhenUriIsEmpty(LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.stats.ViewStats(h.app, h.uri, count (h.ip)) "
            + "from Hit h "
            + "where h.timestamp between ?1 and ?2 "
            + "and h.uri in (?3)"
            + "group by h.app, h.uri "
            + "order by count (h.ip) desc")
    List<ViewStats> findAllHitsWhenStarEndUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
