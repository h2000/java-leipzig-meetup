all:
	bundle exec asciidoctor-revealjs tutorial.adoc
	-cp -v *.css ../../../docs
	-mv -v *.html *.png *.svg ../../../docs
	mv -fv ../../../docs/tutorial.html ../../../docs/index.html

serv:
	(cd ../../../docs && http-server)	

rx2.svg: rx2.plantuml
	java -jar plantuml/plantuml.jar -tsvg rx2.plantuml