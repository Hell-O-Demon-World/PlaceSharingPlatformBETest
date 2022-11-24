create table ratepoint
(
    ID          bigint unsigned auto_increment primary key,
    RATINGPOINT float unsigned not null
);

alter table officesharing.place
    add RATEPOINT_ID bigint unsigned null;

alter table officesharing.place
    add constraint FK_RATEPOINT_TO_PLACE_1
        foreign key (RATEPOINT_ID) references officesharing.ratepoint (ID)
            on delete cascade;

