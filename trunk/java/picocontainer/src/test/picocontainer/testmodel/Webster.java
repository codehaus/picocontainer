package picocontainer.testmodel;

import java.util.List;

/**
 * 
 * @author Aslak Hellesoy
 * @version $Revision: 0 $
 */
public class Webster implements Dictionary, Thesaurus {
    public Webster(List list) {
        list.add("webster created");
    }
}
