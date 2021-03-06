
<a name="search"></a>
<h2>Search service support</h2>

<p>
    The full-text search functionality can be accessed with the <code>search</code> variable in the binding
    of Groovlets and templates. You can also specify a special namespace to restrict the searches to that namespace.
</p>

<pre class="brush:groovy">
    // access the search service
    search

    // access the search service for a specific namespace
    search['myNamespace']
</pre>

<p>
    You access a particular search index with the <code>index()</code> method,
    where you specify the index name and consistency mode by passing their values as parameters:
</p>

<pre class="brush:groovy">
    def index = search.index("books", PER_DOCUMENT)
</pre>

<p>
    To add documents to an index, you call the <code>add()</code> method on the index,
    which takes a closure that accepts <code>document(map) {}</code> method calls.
    You can specify several documents in a single <code>add()</code> call,
    by simply making several <code>document()</code> calls inside the closure passed to <code>add()</code>.
</p>

<pre class="brush:groovy">
    def index = search.index("books", PER_DOCUMENT)

    def response = index.add {
        document(id: "1234", locale: US, rank: 3) {
            title text: "Big bad wolf", locale: ENGLISH
            published date: new Date()
            numberOfCopies number: 35
            summary html: "<p>super story</p>", locale: ENGLISH
            description text: "a book for children"
            category atom: "children"
            keyword text: "wolf"
            keyword text: "red hook"
        }
        // other documents with other document(...) {} calls
    }
</pre>

<p>
    The named parameters passed to the <code>document()</code> methods can be <code>id</code>, <code>locale</code>
    and <code>rank</code>.
    Inside the closure, you can have as many field definitions of the form:
    <code>fieldName type: value</code> or with an optional locale:
    <code>fieldName type: value, locale: someLocale</code>.
    Fields can be repeated in order to have multi-valued document fields.
</p>

<p>
    Once you have added documents to an index, you can search for them,
    and iterate over all the results:
</p>

<pre class="brush:groovy">
    // search the index
    def results = index.search("wolf")

    // iterate over all the resuts
    results.each { ScoredDocument doc ->
        assert doc.id == "1234"

        assert doc.title == "Big bad wolf"
        assert doc.numberOfCopies == 35
        assert doc.summary.contains("story")

        assert doc.keyword.size() == 2
        assert "wolf" in doc.keyword
        assert "red hook" in doc.keyword
    }
</pre>

<p>
    As you can see, you can access a document field with the Groovy property notation.
    When a field is multivalued, the <code>doc.field</code> property access actually returns a list of values.
</p>

