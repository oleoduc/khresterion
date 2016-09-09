 
    create table CoherenceObjectEntity (
        id integer not null,
        comparableValue double precision,
        value varchar(1024) not null,
        property_id integer not null,
        primary key (id)
    );
 
    create table EnvironmentEntity (
        id integer not null,
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        modified_by varchar(255),
        name varchar(255) not null,
        primary key (id)
    );
 
    create table EnvironmentEntity_InstanceEntity (
        environmentCollection_id integer not null,
        instanceCollection_id integer not null
    );
 
    create table ExpressionEntity (
        id integer not null,
        isPositive bit not null,
        level integer not null,
        modalityTypeId varchar(255),
        coherenceInformation_id integer not null,
        source_sourceId varchar(255),
        source_sourceType varchar(255),
        primary key (id)
    );
 
    create table InstanceEntity (
        id integer not null,
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        modified_by varchar(255),
        typeId varchar(255) not null,
        primary key (id)
    );
 
    create table Member (
        id bigint not null auto_increment,
        dateCreated bigint,
        dateModified bigint,
        deleted bit,
        firstname varchar(32),
        lastname varchar(32),
        modifiedby varchar(255),
        organisation varchar(255),
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    );
 
    create table PropertyEntity (
        id integer not null,
        path varchar(512) not null,
        instance_id integer not null,
        primary key (id)
    );
 
    create table Role (
        id bigint not null auto_increment,
        external_name varchar(255),
        name varchar(255) not null,
        note varchar(255),
        primary key (id)
    );
 
    create table SourceEntity (
        sourceId varchar(255) not null,
        sourceType varchar(255) not null,
        primary key (sourceId, sourceType)
    );
 
    create table member_role (
        userid bigint not null,
        roleid bigint not null,
        primary key (userid, roleid)
    );
 
    alter table Member 
        add constraint UK_73pcydbur7ap0v0abheab5sfr unique (username);
 
    alter table CoherenceObjectEntity 
        add constraint FK_geewngu8owokforrxfje8iuxc 
        foreign key (property_id) 
        references PropertyEntity (id);
 
    alter table EnvironmentEntity_InstanceEntity 
        add constraint FK_br4et9opxot999c7jp4y0u766 
        foreign key (instanceCollection_id) 
        references InstanceEntity (id);
 
    alter table EnvironmentEntity_InstanceEntity 
        add constraint FK_58363fcyeggous7lsn1fcabsh 
        foreign key (environmentCollection_id) 
        references EnvironmentEntity (id);
 
    alter table ExpressionEntity 
        add constraint FK_bwhisv1itl44ggwim8nuy499k 
        foreign key (source_sourceId, source_sourceType) 
        references SourceEntity (sourceId, sourceType);
 
    alter table PropertyEntity 
        add constraint FK_8mb5sfbme9d64cb4hvw4mqf7b 
        foreign key (instance_id) 
        references InstanceEntity (id);
 
    alter table member_role 
        add constraint FK_cvurj96c89600uemuy0s30w19 
        foreign key (roleid) 
        references Role (id);
    alter table member_role 
        add constraint FK_3o322tcjgn3uglmqbbubb8obr 
        foreign key (userid) 
        references Member (id);
 
    create table SEQUENCE_TABLE (
         SEQ_NAME varchar(255) not null ,
        SEQ_COUNT bigint,
        primary key ( SEQ_NAME ) 
    );
 
    create table hibernate_sequence (
         next_val bigint 
    );
 
    insert into hibernate_sequence values ( 1 );
