package org.codealpha.gmsservice.services;

import org.codealpha.gmsservice.entities.MailLog;
import org.codealpha.gmsservice.repositories.MailLogRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailLogService {
    @Autowired
    private MailLogRespository mailLogRespository;

    public MailLog saveMailLog(MailLog log) {
        return mailLogRespository.save(log);
    }
}