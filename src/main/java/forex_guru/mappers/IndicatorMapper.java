package forex_guru.mappers;

import forex_guru.model.internal.Indicator;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface IndicatorMapper {

    @Insert("INSERT INTO `ForexGuru`.`indicators` " +
            "(`timestamp`, `rate_id`, `type`, `value`) " +
            "VALUES (#{timestamp}, #{rate_id}, #{type}, #{value}); ")
    public boolean insertIndicator(Indicator indicator);

    @Select("SELECT * FROM `ForexGuru`.`indicators` WHERE `rate_id` = \"${rate_id}\" AND `timestamp` = \"${timestamp}\" AND `type` = \"${type}\" LIMIT 1")
    public Indicator findIndicatorByRateIdTimeStampType(@Param("rate_id") int rate_id, @Param("timestamp") long timestamp, @Param("type") String type);
}
