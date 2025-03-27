--创建数据库
create database if not exists ods_ds_hudi;
--创建user_info表
drop table if exists ods_ds_hudi.user_info;
drop table if exists ods_ds_hudi.user_info_ro;
drop table if exists ods_ds_hudi.user_info_rt;
create table if not exists ods_ds_hudi.user_info
(
    `id`           bigint,
    `login_name`   string,
    `nick_name`    string,
    `passwd`       string,
    `name`         string,
    `phone_num`    string,
    `email`        string,
    `head_img`     string,
    `user_level`   string,
    `birthday`     date,
    `gender`       string,
    `create_time`  timestamp,
    `operate_time` timestamp
) using hudi
    options(
        type = 'mor',
        primaryKey = 'id',
        preCombineField = 'operate_time'
    )
    partitioned by (etl_date string);
--插入一条测试数据
insert overwrite table ods_ds_hudi.user_info partition (etl_date = "19971201")
select 6814,
       "89xtog",
       "阿清",
       "",
       "成清",
       "13935394894",
       "89xtog@163.net",
       "",
       "1",
       date("1965-04-26"),
       "M",
       timestamp("1997-04-26 00:00:00"),
       timestamp("1997-04-26 00:00:00");
drop table if exists ods_ds_hudi.sku_info;
drop table if exists ods_ds_hudi.sku_info_ro;
drop table if exists ods_ds_hudi.sku_info_rt;
--创建sku_info 表
create table if not exists ods_ds_hudi.sku_info
(
    `id`              bigint,
    `spu_id`          bigint,
    `price`           decimal(10, 0),
    `sku_name`        string,
    `sku_desc`        string,
    `weight`          decimal(10, 2),
    `tm_id`           bigint,
    `category3_id`    bigint,
    `sku_default_img` string,
    `create_time`     timestamp
) using  hudi
    options(
    type = 'mor',
    primaryKey = 'id',
    preCombineField = 'create_time'
    )
    partitioned by (`etl_date` string);
--插入测试数据
insert overwrite table ods_ds_hudi.sku_info partition (etl_date = "19971201")
select 1,
       1,
       2220,
       "测试",
       "new sku_desc",
       0.24,
       2,
       61,
       "http://AOvKmfRQEBRJJllwCwCuptVAOtBBcIjWeJRsmhbJ",
       timestamp("1997-12-01 12:21:13");

drop table if exists ods_ds_hudi.base_province;
drop table if exists ods_ds_hudi.base_province_ro;
drop table if exists ods_ds_hudi.base_province_rt;
create table if not exists ods_ds_hudi.base_province
(
    `id`          bigint,
    `name`        string,
    `region_id`   string,
    `area_code`   string,
    `iso_code`    string,
    `create_time` timestamp
) using  hudi
    options(
    type = 'mor',
    primaryKey = 'id',
    preCombineField = 'create_time'
    )
    partitioned by (`etl_date` string);

insert overwrite table ods_ds_hudi.base_province partition (etl_date = "19971201")
select 0, "测试", 0, 000000, "测试", timestamp("1997-12-01 00:00:00");

drop table if exists ods_ds_hudi.base_region;
drop table if exists ods_ds_hudi.base_region_ro;
drop table if exists ods_ds_hudi.base_region_rt;
create table if not exists ods_ds_hudi.base_region
(
    `id`          string,
    `region_name` string,
    `create_time` timestamp
) using  hudi
    options(
    type = 'mor',
    primaryKey = 'id',
    preCombineField = 'create_time'
    )
    partitioned by (`etl_date` string);

insert overwrite table ods_ds_hudi.base_region partition (etl_date = "19971201")
select 0, "测试", timestamp("1997-12-01 00:00:00");

drop table if exists ods_ds_hudi.order_info;
drop table if exists ods_ds_hudi.order_info_ro;
drop table if exists ods_ds_hudi.order_info_rt;
create table if not exists ods_ds_hudi.order_info
(
    `id`                    bigint,
    `consignee`             string COMMENT '收货人',
    `consignee_tel`         string COMMENT '收件人电话',
    `final_total_amount`    decimal(16, 2) COMMENT '总金额',
    `order_status`          string COMMENT '订单状态',
    `user_id`               bigint COMMENT '用户id（对应用户表id）',
    `delivery_address`      string COMMENT '送货地址',
    `order_comment`         string COMMENT '订单备注',
    `out_trade_no`          string COMMENT '订单交易编号（第三方支付用)',
    `trade_body`            string COMMENT '订单描述（第三方支付用）',
    `create_time`           timestamp COMMENT '创建时间',
    `operate_time`          timestamp COMMENT '操作时间',
    `expire_time`           timestamp COMMENT '失效时间',
    `tracking_no`           string COMMENT '物流单编号',
    `parent_order_id`       bigint COMMENT '父订单编号',
    `img_url`               string COMMENT '图片路径',
    `province_id`           int COMMENT '省份id（对应省份表id）',
    `benefit_reduce_amount` decimal(16, 2) COMMENT '优惠金额',
    `original_total_amount` decimal(16, 2) COMMENT '原价金额',
    `feight_fee`            decimal(16, 2) COMMENT '运费'
) using  hudi
    options(
    type = 'mor',
    primaryKey = 'id',
    preCombineField = 'operate_time'
    )
    partitioned by (`etl_date` string);

insert overwrite table ods_ds_hudi.order_info partition (etl_date = "19971201")
select 3443,
       "测试",
       13207871570,
       1449.00,
       1005,
       2790,
       "第4大街第5号楼4单元464门",
       "描述345855",
       214537477223728,
       "小米Play 流光渐变AI双摄 4GB+64GB 梦幻蓝 全网通4G 双卡双待 小水滴全面屏拍照游戏智能手机等1件商品",
       timestamp("1997-04-25 18:47:14"),
       timestamp("1997-04-26 18:59:01"),
       timestamp("1997-04-25 19:02:14"),
       "",
       null,
       "http://img.gmall.com/117814.jpg,20,0.00,1442.00,7.00",
       20,
       0.00,
       1442.00,
       7.00;


drop table if exists ods_ds_hudi.order_detail;
drop table if exists ods_ds_hudi.order_detail_ro;
drop table if exists ods_ds_hudi.order_detail_rt;
create table if not exists ods_ds_hudi.order_detail
(
    `id`          bigint COMMENT '主键',
    `order_id`    bigint COMMENT '订单编号（对应订单信息表id）',
    `sku_id`      bigint COMMENT '商品id（对应商品表id）',
    `sku_name`    string COMMENT '商品名称',
    `img_url`     string COMMENT '图片路径',
    `order_price` decimal(10, 2) COMMENT '购买价格(下单时的商品价格）',
    `sku_num`     string COMMENT '购买数量',
    `create_time` timestamp COMMENT '创建时间',
    `source_type` string COMMENT '来源类型',
    `source_id`   bigint COMMENT '来源编号'
) using  hudi
    options(
    type = 'mor',
    primaryKey = 'id',
    preCombineField = 'create_time'
    )
    partitioned by (`etl_date` string);

insert overwrite table ods_ds_hudi.order_detail partition (etl_date = "19971201")
select 8621,
       3443,
       4,
       "测试",
       "http://SXlkutIjYpDWWTEpNUiisnlsevOHVElrdngQLgyZ",
       1442.00,
       1,
       timestamp("1997-12-01 18:47:14"),
       2401,
       null;