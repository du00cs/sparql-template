## SPARQL Template

[![Build status](https://travis-ci.org/gushakov/sparql-template.svg?branch=master)](https://travis-ci.org/gushakov/sparql-template)

Small library for traversing an RDF store using automatic mapping of triples to annotated POJOs.

## Highlights

 * Support of any store exposing HTTP SPARQL endpoint
 * Uses [Jena API](https://jena.apache.org/) to load and process RDF triples
 * Uses [MappingContext](https://github.com/spring-projects/spring-data-commons/blob/master/src/main/java/org/springframework/data/mapping/context/MappingContext.java) from Spring Data Commons to process class annotations
 * On-demand (lazy) loading of relations using automatic proxying with [ByteBuddy](http://bytebuddy.net/)
 * Easy extension for conversion from literal values to custom Java types
 
## Examples

A simple set of domain POJOs.

```java
@Rdf
public class Country {

    @Predicate(DBP)
    private String commonName;

    public String getCommonName() {
        return commonName;
    }

}

@Rdf
public class Person {

    @Predicate(DBP)
    private String birthName;

    @Predicate(DBO)
    @Relation
    private Country citizenship;

    @Predicate(DBP)
    @Relation
    private Collection<Person> spouse;

    public String getBirthName() {
        return birthName;
    }

    public Country getCitizenship() {
        return citizenship;
    }

    public Collection<Person> getSpouse() {
        return spouse;
    }

}
```
Example of loading RDF information for a well-known resource of type http://schema.org/person from a public SPARQL endpoint available from [DBPedia](http://dbpedia.org/sparql).
```java
final SparqlTemplate sparqlTemplate = new SparqlTemplate("https://dbpedia.org/sparql");
final Person person = sparqlTemplate.load(DBR + ":Angelina_Jolie", Person.class);
assertThat(person.getBirthName()).isEqualTo("Angelina Jolie Voight");
assertThat(person.getCitizenship().getCommonName()).isEqualTo("Cambodia");
```
