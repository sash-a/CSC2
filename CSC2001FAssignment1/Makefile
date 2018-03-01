# binary search program makefile
# Hussein Suleman
# 27 March 2017

JAVAC=/usr/bin/javac
BUILD_DIR=bin
CLASSPATH = bin
SRC_DIR = src

_SRC = Person BinaryTreeNode BTQueueNode BTQueue BinaryTree BinarySearchTree PrintIt SearchIt SearchItLinear

CLASSFILES = bin/Person.class bin/BinaryTreeNode.class bin/BTQueueNode.class bin/BTQueue.class bin/BinaryTree.class bin/BinarySearchTree.class bin/PrintIt.class bin/SearchIt.class bin/SearchItLinear.class

default: $(CLASSFILES)

runSearchIt:
	java -cp ${BUILD_DIR} SearchIt

runPrintIt:
	java -cp ${BUILD_DIR} PrintIt

runSearchItLinear:
	java -cp ${BUILD_DIR} SearchItLinear

clean:
	rm $(CLASSFILES)

${BUILD_DIR}/%.class: ${SRC_DIR}/%.java
	${JAVAC} -cp ${CLASSPATH} -d ${BUILD_DIR} $<
