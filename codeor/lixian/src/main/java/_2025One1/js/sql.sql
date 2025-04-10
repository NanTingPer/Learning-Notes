-- 计算2 创建表
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
primaryKey='uuid',
type='mor',
preCombineField='total_count')
partitioned by (year, month);

-- 计算2 查询
select uuid, province_id, province_name, region_id, region_name, cast(total_amount as bigint) as total_amount, total_count, sequence, year, month from dws_ds_hudi.province_consumption_day_aggr order by total_count desc, total_amount desc, province_id desc limit 5;


select
    uuid, province_id, province_name, region_id, region_name, cast(total_amount as bigint) as total_amount, total_count, sequence, year, month
from
    dws_ds_hudi.province_consumption_day_aggr
order by
    total_count desc,
    total_amount desc,
    province_id desc
limit 5;


--计算3 创表
create table shtd_result.topten( topquantityid Int32, topquantityname TEXT, topquantity Int32, toppriceid TEXT, toppricename TEXT, topprice DECIMAL(20 , 2), sequence Int32 )

create table shtd_result.topten(
    topquantityid Int32,
    topquantityname TEXT,
    topquantity Int32,
    toppriceid TEXT,
    toppricename TEXT,
    topprice DECIMAL(20 , 2),
    sequence Int32
)engine
    MergeTree
    order by sequence;

--计算3 查询
select * from shtd_result.topten order by sequence asc limit 5;



--计算4 创表
create table shtd_result.nationmedian( provinceid Int32, provincename TEXT, regionid Int32, regionname TEXT, provincemedian DOUBLE, regionmedian DOUBLE )engine MergeTree order by provinceid
create table shtd_result.nationmedian(
    provinceid Int32,
    provincename TEXT,
    regionid Int32,
    regionname TEXT,
    provincemedian DOUBLE,
    regionmedian DOUBLE
)engine MergeTree
order by provinceid

--计算4 查询
select * from shtd_result.nationmedian order by provinceid asc limit 5;

select
    *
from
    shtd_result.nationmedian
order by
    provinceid asc
limit 5;
