package com.yuzhi.dtadmin.service.admin;

import com.yuzhi.dtadmin.domain.AuditEvent;
import com.yuzhi.dtadmin.domain.enumeration.AuditOutcome;
import com.yuzhi.dtadmin.repository.AuditEventRepository;
import com.yuzhi.dtadmin.service.dto.AuditEventDTO;
import com.yuzhi.dtadmin.service.dto.AuditQueryCriteria;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuditService {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    public void record(
        String action,
        String resource,
        AuditOutcome outcome,
        String detailJson,
        HttpServletRequest request,
        String actor,
        Collection<String> roles
    ) {
        AuditEvent event = new AuditEvent();
        event.setAction(action);
        event.setResource(resource);
        event.setOutcome(outcome);
        event.setDetailJson(detailJson);
        event.setTimestamp(Instant.now());
        event.setActor(actor);
        event.setActorRoles(roles != null ? String.join(",", roles) : null);
        if (request != null) {
            event.setIp(request.getRemoteAddr());
            event.setUserAgent(request.getHeader("User-Agent"));
        }
        auditEventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEventDTO> findByCriteria(AuditQueryCriteria criteria) {
        Instant from = criteria.getFrom() != null ? criteria.getFrom() : Instant.now().minusSeconds(86400);
        Instant to = criteria.getTo() != null ? criteria.getTo() : Instant.now();
        return auditEventRepository
            .findByTimestampBetween(from, to)
            .stream()
            .filter(e -> matches(criteria, e))
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public byte[] export(List<AuditEventDTO> events, String format) {
        if ("csv".equalsIgnoreCase(format)) {
            StringBuilder builder = new StringBuilder();
            builder.append("id,timestamp,actor,roles,ip,action,resource,outcome\n");
            for (AuditEventDTO e : events) {
                builder
                    .append(e.getId())
                    .append(',')
                    .append(escapeCsv(ISO_FORMATTER.format(e.getTimestamp())))
                    .append(',')
                    .append(escapeCsv(e.getActor()))
                    .append(',')
                    .append(escapeCsv(e.getActorRoles()))
                    .append(',')
                    .append(escapeCsv(e.getIp()))
                    .append(',')
                    .append(escapeCsv(e.getAction()))
                    .append(',')
                    .append(escapeCsv(e.getResource()))
                    .append(',')
                    .append(escapeCsv(e.getOutcome() != null ? e.getOutcome().name() : null))
                    .append('\n');
            }
            return builder.toString().getBytes(StandardCharsets.UTF_8);
        }
        String json = events
            .stream()
            .map(e ->
                "{\"id\":" +
                e.getId() +
                ",\"timestamp\":\"" +
                ISO_FORMATTER.format(e.getTimestamp()) +
                "\",\"actor\":\"" +
                safe(e.getActor()) +
                "\",\"roles\":\"" +
                safe(e.getActorRoles()) +
                "\",\"ip\":\"" +
                safe(e.getIp()) +
                "\",\"action\":\"" +
                safe(e.getAction()) +
                "\",\"resource\":\"" +
                safe(e.getResource()) +
                "\",\"outcome\":\"" +
                (e.getOutcome() != null ? e.getOutcome().name() : "") +
                "\"}"
            )
            .collect(Collectors.joining(",", "[", "]"));
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private boolean matches(AuditQueryCriteria criteria, AuditEvent event) {
        if (criteria.getActor() != null && !Objects.equals(criteria.getActor(), event.getActor())) {
            return false;
        }
        if (criteria.getAction() != null && !Objects.equals(criteria.getAction(), event.getAction())) {
            return false;
        }
        if (criteria.getResource() != null && !Objects.equals(criteria.getResource(), event.getResource())) {
            return false;
        }
        if (criteria.getOutcome() != null && !Objects.equals(criteria.getOutcome(), event.getOutcome())) {
            return false;
        }
        return true;
    }

    private AuditEventDTO toDto(AuditEvent event) {
        AuditEventDTO dto = new AuditEventDTO();
        dto.setId(event.getId());
        dto.setTimestamp(event.getTimestamp());
        dto.setActor(event.getActor());
        dto.setActorRoles(event.getActorRoles());
        dto.setIp(event.getIp());
        dto.setUserAgent(event.getUserAgent());
        dto.setAction(event.getAction());
        dto.setResource(event.getResource());
        dto.setOutcome(event.getOutcome());
        dto.setDetailJson(event.getDetailJson());
        return dto;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }

    private String safe(String value) {
        return value == null ? "" : value.replace("\"", "\\\"");
    }
}
