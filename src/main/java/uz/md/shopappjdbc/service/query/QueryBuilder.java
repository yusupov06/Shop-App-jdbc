package uz.md.shopappjdbc.service.query;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class QueryBuilder {

    private final StringBuilder sb = new StringBuilder();

    @Getter
    @Setter
    private String tableName;
    private List<String> or;
    private List<String> and;
    private Sort sort;
    private Pagination page;

    public QueryBuilder(String tableName) {
        this.tableName = tableName;
    }

    public QueryBuilder or(List<String> or) {
        this.or = or;
        return this;
    }

    public QueryBuilder and(List<String> and) {
        this.and = and;
        return this;
    }

    public QueryBuilder sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    public QueryBuilder page(Pagination page) {
        this.page = page;
        return this;
    }

    public String build() {

        sb.append("select * from ")
                .append(tableName);

        if (or != null) {
            sb.append(" where ");
            or.forEach(s -> sb.append(s).append(" or "));
            int i = sb.lastIndexOf("or");
            sb.delete(i, i + 2);
        }

        if (and != null) {
            sb.append(" where ");
            and.forEach(s -> sb.append(s).append(" and "));
            int i = sb.lastIndexOf("and");
            sb.delete(i, i + 3);
        }

        if (sort != null) {
            sb.append(" order by ")
                    .append(sort.getOrderBy())
                    .append(" ")
                    .append(sort.getDirection().toString())
                    .append(" ");
        }

        if (page != null) {

            sb.append(" limit ")
                    .append(page.getPageCount())
                    .append(" offset ")
                    .append(page.getPage()*page.getPageCount());

        }

        return sb.toString();
    }

}
