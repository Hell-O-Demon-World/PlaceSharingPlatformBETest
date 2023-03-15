package com.golfzonaca.officesharingplatform;

import com.golfzonaca.officesharingplatform.domain.Role;
import com.golfzonaca.officesharingplatform.domain.RoomKind;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;

@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.roleInit();
        initService.roomKindInit();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void roleInit() {
            Role userRole = Role.userRole();
            em.persist(userRole);
        }

        public void roomKindInit() {
            RoomKind desk = RoomKind.desk();
            em.persist(desk);

            RoomKind meetingroom4 = RoomKind.meetingroom4();
            em.persist(meetingroom4);

            RoomKind meetingroom6 = RoomKind.meetingroom6();
            em.persist(meetingroom6);

            RoomKind meetingroom10 = RoomKind.meetingroom10();
            em.persist(meetingroom10);

            RoomKind meetingroom20 = RoomKind.meetingroom20();
            em.persist(meetingroom20);

            RoomKind office20 = RoomKind.office20();
            em.persist(office20);

            RoomKind office40 = RoomKind.office40();
            em.persist(office40);

            RoomKind office70 = RoomKind.office70();
            em.persist(office70);

            RoomKind office100 = RoomKind.office100();
            em.persist(office100);
        }
    }
}


