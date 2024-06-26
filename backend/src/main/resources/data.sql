drop table if exists homepage_image;

drop table if exists homepage_announcement;

drop table if exists tutoring_reservation;

drop table if exists tutor;

drop table if exists counseling_reservation;

drop table if exists counseling_slot;

drop table if exists chat_member;

drop table if exists chat_message;

drop table if exists chat;

drop table if exists course_member;

drop table if exists course_announcement;

drop table if exists attempt_attachment;

drop table if exists assignment_attempt;

drop table if exists "user";

drop table if exists course_assignment;

drop table if exists learning_course;

drop table if exists "address";

drop table if exists "category";

drop table if exists "collect";

drop table if exists "comment";

drop table if exists "feedback";

drop table if exists "goods";

drop table if exists "help";

drop table if exists "likes";

drop table if exists "notice";

drop table if exists "orders";



CREATE TABLE "user"
(
    id       BIGSERIAL,
    name     VARCHAR(32) NOT NULL,
    password VARCHAR(32) NOT NULL,
    token    VARCHAR(32) NOT NULL,
    type     VARCHAR(32) NOT NULL,
    avatar   VARCHAR(256),
    nickname VARCHAR(255),
    email    VARCHAR(255),
    phone    VARCHAR(32),
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE tutor
(
    id          BIGINT        NOT NULL,
    name        VARCHAR(32)   NOT NULL,
    photo       VARCHAR(256)  NOT NULL,
    description VARCHAR(1024) NOT NULL,
    mail        VARCHAR(128)  NOT NULL,
    department  VARCHAR(128)  NOT NULL,
    position    VARCHAR(128)  NOT NULL,
    FOREIGN KEY (id) REFERENCES "user" (id),
    UNIQUE (id)
);

CREATE TABLE tutoring_reservation
(
    id           BIGSERIAL,
    tutor_id     BIGINT      NOT NULL,
    applicant_id BIGINT,
    time         BIGINT      NOT NULL,
    status       VARCHAR(32) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tutor_id) REFERENCES tutor (id),
    FOREIGN KEY (applicant_id) REFERENCES "user" (id)
);

CREATE TABLE counseling_slot
(
    id         BIGSERIAL,
    start_time BIGINT NOT NULL,
    end_time   BIGINT NOT NULL,
    capacity   INT    NOT NULL,
    available  INT    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE counseling_reservation
(
    id           BIGSERIAL,
    applicant_id BIGINT      NOT NULL,
    slot_id      BIGINT      NOT NULL,
    status       VARCHAR(32) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (applicant_id) REFERENCES "user" (id),
    FOREIGN KEY (slot_id) REFERENCES counseling_slot (id)
);

CREATE TABLE chat
(
    id BIGSERIAL,
    PRIMARY KEY (id)
);

CREATE TABLE chat_member
(
    chat_id    BIGINT,
    user_id    BIGINT,
    read_until BIGINT DEFAULT 0,
    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    UNIQUE (chat_id, user_id)
);

CREATE TABLE chat_message
(
    chat_id   BIGINT,
    sender_id BIGINT,
    content   VARCHAR NOT NULL,
    time      BIGINT       NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chat (id),
    FOREIGN KEY (sender_id) REFERENCES "user" (id)
);

CREATE TABLE learning_course
(
    id   BIGSERIAL,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE course_member
(
    course_id BIGINT NOT NULL,
    user_id   BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES learning_course (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id),
    UNIQUE (course_id, user_id)
);

CREATE TABLE course_announcement
(
    id        BIGSERIAL    NOT NULL,
    course_id BIGINT       NOT NULL,
    title     VARCHAR(100) NOT NULL,
    content   TEXT         NOT NULL,
    time      BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES learning_course (id)
);

CREATE TABLE course_assignment
(
    id          BIGSERIAL        NOT NULL,
    course_id   BIGINT           NOT NULL,
    name        VARCHAR(32)      NOT NULL,
    instruction TEXT             NOT NULL,
    max_score   DOUBLE PRECISION NOT NULL,
    weight      DOUBLE PRECISION NOT NULL,
    due         BIGINT           NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (course_id) REFERENCES learning_course (id)
);

CREATE TABLE assignment_attempt
(
    id            BIGSERIAL NOT NULL,
    assignment_id BIGINT    NOT NULL,
    user_id       BIGINT    NOT NULL,
    score         DOUBLE PRECISION,
    feedback      TEXT,
    PRIMARY KEY (id),
    FOREIGN KEY (assignment_id) REFERENCES course_assignment (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);

CREATE TABLE attempt_attachment
(
    id         BIGSERIAL    NOT NULL,
    attempt_id BIGINT       NOT NULL,
    file_path  VARCHAR(256) NOT NULL,
    file_name  VARCHAR(32)  NOT NULL,
    file_type  VARCHAR(32)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (attempt_id) REFERENCES assignment_attempt (id)
);

CREATE TABLE homepage_image
(
    id   BIGSERIAL    NOT NULL,
    path VARCHAR(256) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE homepage_announcement
(
    id      BIGSERIAL    NOT NULL,
    title   VARCHAR(256) NOT NULL,
    time    BIGINT       NOT NULL,
    content TEXT         NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE "address"
(
    "id"      SERIAL NOT NULL,
    "name"    varchar(255),
    "address" varchar(255),
    "phone"   varchar(255),
    "user_id" int4,
    PRIMARY KEY ("id")
);
COMMENT ON COLUMN "address"."id" IS 'ID';
COMMENT ON COLUMN "address"."name" IS '联系人';
COMMENT ON COLUMN "address"."address" IS '联系地址';
COMMENT ON COLUMN "address"."phone" IS '联系电话';
COMMENT ON COLUMN "address"."user_id" IS '关联用户';
COMMENT ON TABLE "address" IS '收货地址';

CREATE TABLE "category"
(
    "id"   SERIAL NOT NULL,
    "name" varchar(255),
    CONSTRAINT "_copy_14" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "category"."id" IS 'ID';
COMMENT ON COLUMN "category"."name" IS '名称';
COMMENT ON TABLE "category" IS '分类';

CREATE TABLE "collect"
(
    "id"      SERIAL NOT NULL,
    "fid"     int4,
    "user_id" int4,
    "module"  varchar(255),
    CONSTRAINT "_copy_10" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "collect"."id" IS 'ID';
COMMENT ON COLUMN "collect"."fid" IS '关联ID';
COMMENT ON COLUMN "collect"."user_id" IS '收藏人ID';
COMMENT ON COLUMN "collect"."module" IS '模块';
COMMENT ON TABLE "collect" IS '收藏';

CREATE TABLE "comment"
(
    "id"      SERIAL NOT NULL,
    "content" varchar(500),
    "user_id" int4,
    "pid"     int4,
    "time"    varchar(255),
    "fid"     int4,
    "module"  varchar(255),
    "root_id" int4,
    CONSTRAINT "_copy_9" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "comment"."id" IS 'ID';
COMMENT ON COLUMN "comment"."content" IS '内容';
COMMENT ON COLUMN "comment"."user_id" IS '评论人';
COMMENT ON COLUMN "comment"."pid" IS '父级ID';
COMMENT ON COLUMN "comment"."time" IS '评论时间';
COMMENT ON COLUMN "comment"."fid" IS '关联ID';
COMMENT ON COLUMN "comment"."module" IS '模块';
COMMENT ON COLUMN "comment"."root_id" IS '根节点ID';
COMMENT ON TABLE "comment" IS '评论表';

CREATE TABLE "feedback"
(
    "id"         SERIAL       NOT NULL,
    "title"      varchar(255),
    "content"    varchar(500),
    "phone"      varchar(255),
    "email"      varchar(255),
    "reply"      varchar(255),
    "createtime" varchar(255) NOT NULL,
    "user_id"    int4,
    CONSTRAINT "_copy_8" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "feedback"."id" IS 'ID';
COMMENT ON COLUMN "feedback"."title" IS '主题';
COMMENT ON COLUMN "feedback"."content" IS '内容';
COMMENT ON COLUMN "feedback"."phone" IS '联系方式';
COMMENT ON COLUMN "feedback"."email" IS '邮箱';
COMMENT ON COLUMN "feedback"."reply" IS '回复';
COMMENT ON COLUMN "feedback"."createtime" IS '创建时间';
COMMENT ON COLUMN "feedback"."user_id" IS '提交人ID';
COMMENT ON TABLE "feedback" IS '反馈信息';

CREATE TABLE "goods"
(
    "id"          SERIAL NOT NULL,
    "name"        varchar(255),
    "price"       numeric(10, 2),
    "content"     text,
    "address"     varchar(255),
    "img"         varchar(255),
    "date"        varchar(255),
    "status"      varchar(255),
    "category"    varchar(255),
    "user_id"     int4,
    "sale_status" varchar(255),
    "read_count"  int4,
    CONSTRAINT "_copy_7" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "goods"."id" IS 'ID';
COMMENT ON COLUMN "goods"."name" IS '名称';
COMMENT ON COLUMN "goods"."price" IS '价格';
COMMENT ON COLUMN "goods"."content" IS '详情';
COMMENT ON COLUMN "goods"."address" IS '发货地址';
COMMENT ON COLUMN "goods"."img" IS '图片';
COMMENT ON COLUMN "goods"."date" IS '上架日期';
COMMENT ON COLUMN "goods"."status" IS '审核状态';
COMMENT ON COLUMN "goods"."category" IS '分类';
COMMENT ON COLUMN "goods"."user_id" IS '所属用户ID';
COMMENT ON COLUMN "goods"."sale_status" IS '上架状态';
COMMENT ON COLUMN "goods"."read_count" IS '浏览量';
COMMENT ON TABLE "goods" IS '二手商品';

CREATE TABLE "help"
(
    "id"      SERIAL NOT NULL,
    "title"   varchar(255),
    "content" varchar(255),
    "img"     varchar(255),
    "status"  varchar(255),
    "user_id" int4,
    "time"    varchar(255),
    "solved"  varchar(255),
    CONSTRAINT "_copy_6" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "help"."id" IS 'ID';
COMMENT ON COLUMN "help"."title" IS '标题';
COMMENT ON COLUMN "help"."content" IS '内容';
COMMENT ON COLUMN "help"."img" IS '图片';
COMMENT ON COLUMN "help"."status" IS '状态';
COMMENT ON COLUMN "help"."user_id" IS '用户ID';
COMMENT ON COLUMN "help"."time" IS '发布时间';
COMMENT ON COLUMN "help"."solved" IS '是否解决';
COMMENT ON TABLE "help" IS '求助信息';

CREATE TABLE "likes"
(
    "id"      SERIAL NOT NULL,
    "fid"     int4,
    "user_id" int4,
    "module"  varchar(255),
    CONSTRAINT "_copy_5" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "likes"."id" IS 'ID';
COMMENT ON COLUMN "likes"."fid" IS '关联ID';
COMMENT ON COLUMN "likes"."user_id" IS '点赞人ID';
COMMENT ON COLUMN "likes"."module" IS '模块';
COMMENT ON TABLE "likes" IS '点赞';

CREATE TABLE "notice"
(
    "id"      SERIAL NOT NULL,
    "title"   varchar(255),
    "content" varchar(255),
    "time"    varchar(255),
    "user"    varchar(255),
    CONSTRAINT "_copy_4" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "notice"."id" IS 'ID';
COMMENT ON COLUMN "notice"."title" IS '标题';
COMMENT ON COLUMN "notice"."content" IS '内容';
COMMENT ON COLUMN "notice"."time" IS '创建时间';
COMMENT ON COLUMN "notice"."user" IS '创建人';
COMMENT ON TABLE "notice" IS '公告信息表';

CREATE TABLE "orders"
(
    "id"         SERIAL NOT NULL,
    "goods_name" varchar(255),
    "goods_img"  varchar(255),
    "order_no"   varchar(255),
    "total"      numeric(10, 2),
    "time"       varchar(255),
    "pay_no"     varchar(255),
    "pay_time"   varchar(255),
    "user_id"    int4,
    "address"    varchar(255),
    "phone"      varchar(255),
    "user_name"  varchar(255),
    "status"     varchar(255),
    "sale_id"    int4,
    CONSTRAINT "_copy_3" PRIMARY KEY ("id")
);
COMMENT ON COLUMN "orders"."id" IS 'ID';
COMMENT ON COLUMN "orders"."goods_name" IS '商品名称';
COMMENT ON COLUMN "orders"."goods_img" IS '商品图片';
COMMENT ON COLUMN "orders"."order_no" IS '订单编号';
COMMENT ON COLUMN "orders"."total" IS '总价';
COMMENT ON COLUMN "orders"."time" IS '下单时间';
COMMENT ON COLUMN "orders"."pay_no" IS '支付单号';
COMMENT ON COLUMN "orders"."pay_time" IS '支付时间';
COMMENT ON COLUMN "orders"."user_id" IS '下单人ID';
COMMENT ON COLUMN "orders"."address" IS '收货地址';
COMMENT ON COLUMN "orders"."phone" IS '联系方式';
COMMENT ON COLUMN "orders"."user_name" IS '收货人名称';
COMMENT ON COLUMN "orders"."status" IS '订单状态';
COMMENT ON COLUMN "orders"."sale_id" IS '卖家ID';
COMMENT ON TABLE "orders" IS '订单信息';


INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100000, 'admin', '12345678', '12345678901234567890123456789012', 'admin', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100001, 'user1', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100002, 'user2', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100003, 'user3', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
INSERT INTO "user" (id, name, password, token, type, avatar, nickname, email, phone)
VALUES (100004, 'user4', '12345678', '12345678901234567890123456789012', 'user', null, '', '', '');
