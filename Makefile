all:
	mkdir -p target
	scalac -d target src/stasiak/karol/fimpp/*
	cd target && jar cfe ../bin/fimpp.jar stasiak.karol.fimpp.Main stasiak

clean:
	rm -r target
