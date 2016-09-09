 
    create table dbo.CoherenceObjectEntity (
        id int not null,
        comparableValue double precision,
        value varchar(1024) not null,
        property_id int not null,
        primary key (id)
    )
 
    create table dbo.EnvironmentEntity (
        id int identity not null,
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        modified_by varchar(255),
        name varchar(255) not null,
        primary key (id)
    )
 
    create table dbo.EnvironmentEntity_InstanceEntity (
        environmentCollection_id int not null,
        instanceCollection_id int not null
    )
 
create table dbo.ExpressionEntity (
        id int not null,
        isPositive bit not null,
        level int not null,
        modalityTypeId varchar(255),
        coherenceInformation_id int not null,
        source_sourceId varchar(255),
        source_sourceType varchar(255),
        primary key (id)
    )
    
    create table dbo.InstanceEntity (
        id int not null,
        created_by varchar(255),
        date_created bigint,
        date_modified bigint,
        modified_by varchar(255),
        typeId varchar(255) not null,
        primary key (id)
    )
 
    create table dbo.Member (
        id bigint identity not null,
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
    )
 
    create table dbo.PropertyEntity (
        id int not null,
        path varchar(512) not null,
        instance_id int not null,
        primary key (id)
    )
 
    create table dbo.Role (
        id bigint identity not null,
        external_name varchar(255),
        name varchar(255) not null,
        note varchar(255),
        primary key (id)
    )
 
    create table dbo.SourceEntity (
        sourceId varchar(255) not null,
        sourceType varchar(255) not null,
        primary key (sourceId, sourceType)
    )
 
    create table dbo.member_role (
        userid bigint not null,
        roleid bigint not null,
        primary key (userid, roleid)
    )

    alter table dbo.Member 
        add constraint UK_73pcydbur7ap0v0abheab5sfr unique (username)
 
    alter table dbo.CoherenceObjectEntity 
        add constraint FK_geewngu8owokforrxfje8iuxc 
        foreign key (property_id) 
        references dbo.PropertyEntity
 
    alter table dbo.EnvironmentEntity_InstanceEntity 
        add constraint FK_br4et9opxot999c7jp4y0u766 
        foreign key (instanceCollection_id) 
        references dbo.InstanceEntity
 
    alter table dbo.EnvironmentEntity_InstanceEntity 
        add constraint FK_58363fcyeggous7lsn1fcabsh 
        foreign key (environmentCollection_id) 
        references dbo.EnvironmentEntity
 
    alter table dbo.ExpressionEntity 
        add constraint FK_bwhisv1itl44ggwim8nuy499k 
        foreign key (source_sourceId, source_sourceType) 
        references dbo.SourceEntity
 
    alter table dbo.PropertyEntity 
        add constraint FK_8mb5sfbme9d64cb4hvw4mqf7b 
        foreign key (instance_id) 
        references dbo.InstanceEntity
 
    alter table dbo.member_role 
        add constraint FK_cvurj96c89600uemuy0s30w19 
        foreign key (roleid) 
        references dbo.Role
 
    alter table dbo.member_role 
        add constraint FK_3o322tcjgn3uglmqbbubb8obr 
        foreign key (userid) 
        references dbo.Member
 
    create table dbo.SEQUENCE_TABLE (
        SEQ_NAME varchar(255) not null ,
        SEQ_COUNT bigint,
        primary key ( SEQ_NAME ) 
    )   
    
  create table dbo.hibernate_sequence (
         next_val bigint 
    )GO
    
    insert into dbo.hibernate_sequence values ( 1 )
    GO