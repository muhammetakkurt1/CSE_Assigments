JC = javac
JFLAGS = -classpath .
JD = javadoc
JDFLAGS = -protected -splitindex -use -author -version -d ./javadoc
RM = rm
JR = java -Xint

CLASSES = \
		Main.java \
		Stock.java \
		AVLTree.java \
		StockDataManager.java \
		GUIVisualization.java \
		RandomCommandGenerator.java


all : classes

run :
	$(JR) Main


classes : $(CLASSES:.java=.class)

%.class : %.java
	$(JC) $(JFLAGS) $<

doc:
	$(JD) $(JDFLAGS) $(CLASSES)

clean:
	$(RM) *.class

cleandoc:
	$(RM) -r ./javadoc
