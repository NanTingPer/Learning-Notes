
--这里是样卷2的计算创表
create table dws_ds_hudi.user_consumption_day_aggr2(
uuid string,
user_id int,
user_name string,
total_amount double,
total_count int,
year int,
month int,
day int)
using hudi
tblproperties (
type='mor',
primaryKey='uuid',
preCombineField='total_count')
partitioned by(year ,month ,day )
location '/user/hive/warehouse/dws_ds_hudi.db/user_consumption_day_aggr2';


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
    month int)
using hudi
tblproperties(
    type = 'mor',
    primaryKey = 'uuid',
    preCombineField = 'total_count')
partitioned by(year, month)


select
    uuid,
    province_id,
    province_name,
    region_id,
    region_name,
    CAST(total_amount as bigint) as total_amount,
    total_count,
    sequence,
    year,
    month
from dws_ds_hudi.province_consumption_day_aggr
order by
    total_count desc,
    total_amount desc,
    province_id desc
limit 5


--这里是样卷6的创表
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
    month int)
using hudi
tblproperties(
    type = 'mor',
    primaryKey = 'uuid',
    preCombineField = 'total_count'
)
partitioned by(year, month)

--查询
select uuid, province_id, province_name, region_id, region_name, total_amount, total_count, sequence, year, month
from dws_ds_hudi.province_consumption_day_aggr
order by total_count desc , total_amount desc, province_name desc
limit 5


create table shtd_result.topten(
    topquantityid Int32,
    topquantityname TEXT,
    topquantity Int32,
    toppriceid TEXT,
    toppricename TEXT,
    topprice DOUBLE,
    sequence Int32
)

CREATE TABLE IF NOT EXISTS shtd_result.topten(
    topquantityid UInt32,
    topquantityname String,
    topquantity UInt32,
    toppriceid UInt32,
    toppricename String,
    topprice Decimal(20,2),
    sequence UInt32
)
    ENGINE = MergeTree()
    ORDER BY sequence;

CREATE TABLE IF NOT EXISTS shtd_result.topten(topquantityid UInt32,topquantityname String,topquantity UInt32,toppriceid UInt32,toppricename String,topprice Decimal(20,2),sequence UInt32) ENGINE = MergeTree() ORDER BY sequence;

select * from shtd_result.topten order by sequence limit 5;


CREATE TABLE shtd_result.nationmedian (
    provinceid Int32,
    provincename String,
    regionid Int32,
    regionname String,
    provincemedian Float64,
    regionmedian Float64
) ENGINE = MergeTree()
ORDER BY provinceid, regionid;

CREATE TABLE shtd_result.nationmedian (provinceid Int32,provincename String,regionid Int32,regionname String,provincemedian Float64,regionmedian Float64 ) ENGINE = MergeTree() ORDER BY provinceid, regionid;
select * from shtd_result.nationmedian order by regionid, provinceid limit 5;
