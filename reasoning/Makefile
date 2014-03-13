
JAVA_CP = "../../../apache-jena-2.11.0/lib/*:."
SCHEMA_FILE = ../family.ttl
QUERY_FILE = ../oblig4.rq
FOAF_FILE = homer.foaf

java = java -cp $(JAVA_CP)
javac = javac -cp $(JAVA_CP)

%.class: %.java
	@$(javac) $<

homer.foaf: Oblig4.class
	@$(java) $(basename $(<F)) $(SCHEMA_FILE) $(QUERY_FILE) $(FOAF_FILE) $@
