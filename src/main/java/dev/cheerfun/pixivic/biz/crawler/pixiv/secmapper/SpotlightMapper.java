package dev.cheerfun.pixivic.biz.crawler.pixiv.secmapper;

import dev.cheerfun.pixivic.common.po.Spotlight;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SpotlightMapper {
    @Insert("<script>" +
            "REPLACE  INTO " +
            "spotlights (`spotlight_id`, `title`, `pure_title`, `thumbnail`, `article_url`, `publish_date`, `category`, `subcategory_label`)" +
            "VALUES" +
            "<foreach collection='spotlights' item='spotlight' index='index'  separator=','>" +
            "(#{spotlight.id}," +
            "#{spotlight.title}," +
            "#{spotlight.pureTitle}," +
            "#{spotlight.thumbnail}," +
            "#{spotlight.articleUrl}," +
            "#{spotlight.publishDate}," +
            "#{spotlight.category}," +
            "#{spotlight.subcategoryLabel})" +
            "</foreach>" +
            "</script>")
    int insert(@Param("spotlights") List<Spotlight> spotlights);

    @Insert({
            "<script>",
            "insert IGNORE into spotlight_illust_relation values ",
            "<foreach collection='illustIds' item='illustId' index='index' separator=','>",
            "(#{spotlightId}, #{illustId})",
            "</foreach>",
            "</script>"
    })
    int insertRelation(int spotlightId, List<Integer> illustIds);

    @Select({"\n" +
            "select distinct t.illust_id\n" +
            "from (select spotlights.*,spotlight_illust_relation.illust_id\n" +
            "                from spotlights\n" +
            "                         left join spotlight_illust_relation\n" +
            "                                   on spotlights.spotlight_id = spotlight_illust_relation.spotlight_id\n" +
            "                order by spotlight_illust_relation.spotlight_id desc\n" +
            "               ) t left join illusts on t.illust_id=illusts.illust_id where illusts.illust_id is null"})
    List<Integer> queryIllustIdNotInDb();

    @Select("select * from spotlights")
    @Results({
            @Result(property = "id", column = "spotlight_id"),
            @Result(property = "pureTitle", column = "pure_title"),
            @Result(property = "publishDate", column = "publish_date", typeHandler = org.apache.ibatis.type.LocalDateTypeHandler.class),
            @Result(property = "subcategoryLabel", column = "subcategory_label"),
            @Result(property = "articleUrl", column = "article_url")
    })
    List<Spotlight> queryAll();
}
