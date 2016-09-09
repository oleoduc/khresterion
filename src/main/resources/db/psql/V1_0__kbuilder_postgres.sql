 
    create table public.CoherenceObjectEntity (
        id int4 not null,
        comparableValue float8,
        value varchar(1024) not null,
        property_id int4 not null,
        primary key (id)
    )
 
    create table public.EnvironmentEntity (
        id int4 not null,
        created_by varchar(255),
        date_created int8,
        date_modified int8,
        modified_by varchar(255),
        name varchar(255) not null,
        primary key (id)
    )
 
    create table public.EnvironmentEntity_InstanceEntity (
        environmentCollection_id int4 not null,
        instanceCollection_id int4 not null
    )
 
    create table public.ExpressionEntity (
        id int4 not null,
        isPositive boolean not null,
        level int4 not null,
        modalityTypeId varchar(255),
        coherenceInformation_id int4 not null,
        source_sourceId varchar(255),
        source_sourceType varchar(255),
        primary key (id)
    )
 
    create table public.InstanceEntity (
        id int4 not null,
        created_by varchar(255),
        date_created int8,
        date_modified int8,
        modified_by varchar(255),
        typeId varchar(255) not null,
        primary key (id)
    )
    
    create table public.Member (
        id  bigserial not null,
        dateCreated int8,
        dateModified int8,
        deleted boolean,
        firstname varchar(32),
        lastname varchar(32),
        modifiedby varchar(255),
        organisation varchar(255),
        password varchar(255) not null,
        username varchar(255) not null,
        primary key (id)
    )
 
    create table public.PropertyEntity (
        id int4 not null,
        path varchar(512) not null,
        instance_id int4 not null,
        primary key (id)
    )
 
    create table public.Role (
        id  bigserial not null,
        external_name varchar(255),
        name varchar(255) not null,
        note varchar(255),
        primary key (id)
    )
 
    create table public.SourceEntity (
        sourceId varchar(255) not null,
        sourceType varchar(255) not null,
        primary key (sourceId, sourceType)
    );
 
    create table public.member_role (
        userid int8 not null,
        roleid int8 not null,
        primary key (userid, roleid)
    )
 
    alter table public.Member 
        add constraint UK_73pcydbur7ap0v0abheab5sfr unique (username)
 
    alter table public.CoherenceObjectEntity 
        add constraint FK_geewngu8owokforrxfje8iuxc 
        foreign key (property_id) 
        references public.PropertyEntity
 
    alter table public.EnvironmentEntity_InstanceEntity 
        add constraint FK_br4et9opxot999c7jp4y0u766 
        foreign key (instanceCollection_id) 
        references public.InstanceEntity
 
    alter table public.EnvironmentEntity_InstanceEntity 
        add constraint FK_58363fcyeggous7lsn1fcabsh 
        foreign key (environmentCollection_id) 
        references public.EnvironmentEntity
 
    alter table public.ExpressionEntity 
        add constraint FK_bwhisv1itl44ggwim8nuy499k 
        foreign key (source_sourceId, source_sourceType) 
        references public.SourceEntity
        
    alter table public.PropertyEntity 
        add constraint FK_8mb5sfbme9d64cb4hvw4mqf7b 
        foreign key (instance_id) 
        references public.InstanceEntity
 
    alter table public.member_role 
        add constraint FK_cvurj96c89600uemuy0s30w19 
        foreign key (roleid) 
        references public.Role
 
    alter table public.member_role 
        add constraint FK_3o322tcjgn3uglmqbbubb8obr 
        foreign key (userid) 
        references public.Member
 
   create table public.SEQUENCE_TABLE (
         SEQ_NAME varchar(255) not null ,
        SEQ_COUNT int8,
        primary key ( SEQ_NAME ) 
    );
 
    create sequence public.hibernate_sequence start 1 increment 1;
    