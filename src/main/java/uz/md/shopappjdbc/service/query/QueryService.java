package uz.md.shopappjdbc.service.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.md.shopappjdbc.dtos.request.FilterCriteria;
import uz.md.shopappjdbc.dtos.request.FilterRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSearchRequest;
import uz.md.shopappjdbc.dtos.request.SimpleSortRequest;

import java.util.ArrayList;
import java.util.List;


@Service(value = "queryService")
@RequiredArgsConstructor
public class QueryService {

    public String generateSimpleSearchQuery(String tableName, SimpleSearchRequest searchRequest) {

        QueryBuilder queryBuilder = new QueryBuilder(tableName);
        List<String> orPredicates = new ArrayList<>();
        for (String field : searchRequest.getFields()) {
            orPredicates.add(" upper("+field+")" + " like '%" + searchRequest.getKey().toUpperCase() + "%'");
        }

        queryBuilder.or(orPredicates)
                .sort(new Sort(searchRequest.getSortDirection(), searchRequest.getSortBy()))
                .page(new Pagination(searchRequest.getPage(), searchRequest.getPageCount()));

        return queryBuilder.build();
    }

    public String generateSimpleSortQuery(String tableName, SimpleSortRequest request) {

        QueryBuilder queryBuilder = new QueryBuilder(tableName);

        queryBuilder.sort(new Sort(request.getDirection(), request.getSortBy()))
                .page(new Pagination(request.getPage(), request.getPageCount()));

        return queryBuilder.build();
    }

    public String generateFilterQuery(String tableName, FilterRequest request) {

        QueryBuilder queryBuilder = new QueryBuilder(tableName);

        List<String> predicates = new ArrayList<>();
        for (FilterCriteria filter : request.getFilterCriteria()) {
            predicates.add(
                    switch (filter.getOperation()) {
                        case GREATER_THAN -> filter.getFilterKey() + " > " + filter.getValue();
                        case LESS_THAN -> filter.getFilterKey() + " < " + filter.getValue();
                        case EQUALS -> filter.getFilterKey() + " = " + filter.getValue();
                    }
            );
        }
        queryBuilder
                .and(predicates)
                .page(new Pagination(request.getPage(), request.getPageCount()));
        return queryBuilder.build();
    }


}