select id, sku_desc, dwd_insert_user, dwd_modify_time, etl_date from dwd_ds_hudi.dim_sku_info where id >= 15 and id <= 20 and etl_date='20250410' order by id asc
select count(*) from dwd_ds_hudi.dim_region where etl_date='20250410'

--计算1 创表
drop table dws_ds_hudi.province_consumption_day_aggr;
create table dws_ds_hudi.province_consumption_day_aggr(
    uuid string,
    province_id int,
    province_name string,
    region_id int,
    region_name string,
    total_amount double,
    total_count int,
    sequence int,
    year int,
    month int
)
using hudi
tblproperties (
type='mor',
primaryKey='uuid',
preCombineField='total_count')
partitioned by (year,month);


--计算1 查询
select uuid, province_id, province_name,
       region_id, region_name, CAST(total_amount as bigint) as total_amount,
       total_count, sequence, year, month
from dws_ds_hudi.province_consumption_day_aggr
order by
    total_count desc,
    total_amount desc,
    province_id desc limit 5;


--计算2 创表
drop table shtd_result.topten;
create table shtd_result.topten(topquantityid Int32, topquantityname TEXT, topquantity Int32, toppriceid TEXT, toppricename TEXT, topprice DECIMAL(32,2), sequence Int32)engine MergeTree order by sequence

create table shtd_result.topten(
    topquantityid Int32,
    topquantityname TEXT,
    topquantity Int32,
    toppriceid TEXT,
    toppricename TEXT,
    topprice DECIMAL(32,2),
    sequence Int32) engine MergeTree order by sequence

--计算2 查询
select * from shtd_result.topten order by sequence asc limit5;


--计算3 查询
create table shtd_result.nationmedian(provinceid Int32, provincename TEXT, regionid Int32, regionname TEXT, provincemedian DOUBLE, regionmedian DOUBLE)engine MergeTree order by regionmedian;

create table shtd_result.nationmedian(
    provinceid Int32,
    provincename TEXT,
    regionid Int32,
    regionname TEXT,
    provincemedian DOUBLE,
    regionmedian DOUBLE
)engine MergeTree order by regionmedian;

select * from shtd_result.nationmedian order by regionid asc, provinceid asc limit 5;