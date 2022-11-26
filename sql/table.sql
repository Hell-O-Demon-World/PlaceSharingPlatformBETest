create table address
(
    ID         bigint unsigned auto_increment primary key,
    ADDRESS    varchar(100) not null,
    POSTALCODE varchar(5)   not null
);

create table mileage
(
    ID    bigint unsigned auto_increment primary key,
    POINT bigint unsigned not null
);

create table company
(
    ID              bigint unsigned auto_increment primary key,
    COMPANY_LOGINID varchar(15)  not null,
    COMPANY_PW      varchar(100) not null,
    COMPANY_NAME    varchar(30)  not null,
    COMPANY_TEL     varchar(22)  not null,
    COMPANY_REGNUM  varchar(12)  not null,
    COMPANY_REPNAME varchar(20)  not null,
    ADDRESS_ID      bigint unsigned not null,
    constraint COMPANY_LOGINID unique (COMPANY_LOGINID),
    constraint COMPANY_NAME unique (COMPANY_NAME),
    constraint COMPANY_REGNUM unique (COMPANY_REGNUM),
    constraint COMPANY_TEL unique (COMPANY_TEL),
    constraint FK_ADDRESS_TO_COMPANY_1
        foreign key (ADDRESS_ID)
            references address (ID)
            on delete cascade
);

create table place
(
    ID                bigint unsigned auto_increment primary key,
    COMPANY_ID        bigint unsigned not null,
    PLACE_NAME        varchar(30) not null,
    PLACE_DESCRIPTION varchar(50) not null,
    PLACE_OPENDAYS    varchar(50) not null,
    PLACE_START       time        not null,
    PLACE_END         time        not null,
    PLACE_ADDINFO     varchar(50) not null,
    ADDRESS_ID        bigint unsigned not null,
    constraint PLACE_NAME
        unique (PLACE_NAME),
    constraint FK_ADDRESS_TO_PLACE_1
        foreign key (ADDRESS_ID)
            references address (ID)
            on delete cascade,
    constraint FK_COMPANY_TO_PLACE_1
        foreign key (COMPANY_ID)
            references company (ID)
            on delete cascade
);

create table room_kind
(
    ID        bigint unsigned not null primary key,
    ROOM_TYPE enum ('DESK', 'MEETINGROOM4', 'MEETINGROOM6', 'MEETINGROOM10', 'MEETINGROOM20', 'OFFICE20', 'OFFICE40', 'OFFICE70', 'OFFICE100') not null,
    PRICE     enum ('10000', '20000', '30000', '50000', '100000', '200000', '300000', '500000') not null,
    constraint ROOM_TYPE
        unique (ROOM_TYPE)
);

create table user
(
    ID          bigint unsigned auto_increment primary key,
    MILEAGE_ID  bigint unsigned not null,
    USER_MAIL   varchar(32)  not null,
    USER_PW     varchar(100) not null,
    USER_NAME   varchar(20)  not null,
    USER_TEL    varchar(22)  not null,
    USER_JOB    varchar(20)  not null,
    prefer_type varchar(50)  not null,
    constraint USER_MAIL
        unique (USER_MAIL),
    constraint USER_TEL
        unique (USER_TEL),
    constraint FK_MILEAGE_TO_USER_1
        foreign key (MILEAGE_ID)
            references mileage (ID)
            on delete cascade
);

create table room
(
    ID           bigint unsigned auto_increment primary key,
    ROOM_KIND_ID bigint unsigned not null,
    PLACE_ID     bigint unsigned not null,
    COMPANY_ID   bigint unsigned not null,
    TOTAL_NUM    int(3) unsigned not null,
    ROOM_STATE   tinyint(1) not null,
    constraint FK_COMPANY_TO_ROOM_1
        foreign key (COMPANY_ID)
            references company (ID)
            on delete cascade,
    constraint FK_PLACE_TO_ROOM_1
        foreign key (PLACE_ID)
            references place (ID)
            on delete cascade,
    constraint FK_ROOM_KIND_TO_ROOM_1
        foreign key (ROOM_KIND_ID)
            references room_kind (ID)
            on delete cascade
);

create table reservation
(
    ID            bigint unsigned auto_increment primary key,
    PLACE_ID      bigint unsigned not null,
    USER_ID       bigint unsigned not null,
    ROOM_ID       bigint unsigned not null,
    ROOM_KIND_ID  bigint unsigned not null,
    RES_STARTDATE date not null,
    RES_STARTTIME time not null,
    RES_ENDDATE   date not null,
    RES_ENDTIME   time not null,
    constraint FK_PLACE_TO_RESERVATION_1
        foreign key (PLACE_ID)
            references place (ID)
            on delete cascade,
    constraint FK_ROOM_KIND_TO_RESERVATION_1
        foreign key (ROOM_KIND_ID)
            references room_kind (ID)
            on delete cascade,
    constraint FK_ROOM_TO_RESERVATION_1
        foreign key (ROOM_ID)
            references room (ID)
            on delete cascade,
    constraint FK_USER_TO_RESERVATION_1
        foreign key (USER_ID)
            references user (ID)
            on delete cascade
);

create table payment
(
    ID           bigint unsigned auto_increment primary key,
    USER_ID      bigint unsigned not null,
    ROOM_ID      bigint unsigned not null,
    PAY_DATE     date        not null comment 'API 결제 결과에서 가져오기',
    PAY_TIME     time        not null comment 'API 결제 결과에서 가져오기',
    PAY_PRICE    bigint unsigned not null comment '50000',
    PAY_STATUS   enum ('PREPAYMENT', 'POSTPAYMENT') not null comment '선결제, 현장결제',
    PAY_MILEAGE  bigint unsigned not null,
    PAY_TYPE     enum ('DEPOSIT', 'BALANCE', 'FULLPAYMENT') not null,
    PAY_API_CODE varchar(30) not null comment 'API 결제를 통해 생성되는 거래 번호',
    constraint FK_USER_TO_PAYMENT_1
        foreign key (USER_ID)
            references user (ID)
            on delete cascade,
    constraint FK_ROOM_TO_PAYMENT_1
        foreign key (ROOM_ID)
            references ROOM (ID)
            on delete cascade
);
create table refresh_token
(
    ID            bigint unsigned auto_increment primary key ,
    USER_ID       bigint unsigned not null,
    ENCODED_TOKEN VARCHAR(256) not null,
    constraint FK_USER_TO_REFRESH_TOKEN_1
        foreign key (USER_ID)
            references user (ID)
            on delete cascade
);