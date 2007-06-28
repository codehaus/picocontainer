/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import webwork.action.ActionSupport;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Hits;
import java.io.IOException;
/**
 * base search capability
 *
 * @author    kostik
 * @created   December 2, 2004
 * @version   $Revision$
 */
public abstract class AbstractBaseFindAction extends ActionSupport {

    private Analyzer _analyzer;

    private Searcher _searcher;

    private Hits    _hits;


    /**
     * Constructor for the AbstractBaseFindAction object
     *
     * @param analyzer  Description of Parameter
     * @param searcher  Description of Parameter
     */
    public AbstractBaseFindAction(Searcher searcher, Analyzer analyzer) {
        setSearcher(searcher);
        setAnalyzer(analyzer);
    }


    /**
     * Gets the Hits attribute of the AbstractBaseFindAction object
     *
     * @return   The Hits value
     */
    public Hits getHits() {
        return _hits;
    }


    /**
     * Gets the Analyzer attribute of the AbstractBaseFindAction object
     *
     * @return   The Analyzer value
     */
    public Analyzer getAnalyzer() {
        return _analyzer;
    }


    /**
     * Gets the Searcher attribute of the AbstractBaseFindAction object
     *
     * @return   The Searcher value
     */
    public Searcher getSearcher() {
        return _searcher;
    }


    /**
     * Sets the Hits attribute of the AbstractBaseFindAction object
     *
     * @param hits  The new Hits value
     */
    public void setHits(Hits hits) {
        _hits = hits;
    }


    /**
     * Sets the Searcher attribute of the AbstractBaseFindAction object
     *
     * @param searcher  The new Searcher value
     */
    public void setSearcher(Searcher searcher) {
        _searcher = searcher;
    }


    /**
     * Sets the Analyzer attribute of the AbstractBaseFindAction object
     *
     * @param analyzer  The new Analyzer value
     */
    public void setAnalyzer(Analyzer analyzer) {
        _analyzer = analyzer;
    }


    /**
     * fond documents for query
     *
     * @param query            Description of Parameter
     * @exception IOException  Description of Exception
     */
    public void find(Query query) throws IOException {
        setHits(getSearcher().search(query));
    }
}
