package club.p6e.coat.common.searchable;

import java.io.Serializable;

/**
 * @author lidashuang
 * @version 1.0
 */
public class SearchableContext extends SearchableAbstract<SearchableAbstract.Option> implements Serializable {

    public SearchableContext() {
    }

    public SearchableContext(String relationship) {
        super(relationship);
    }

}
