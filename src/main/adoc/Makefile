all:
	bundle exec asciidoctor-revealjs tutorial.adoc
	cp -v *.html *.png *.css ../../../docs
	mv -fv ../../../docs/tutorial.html ../../../docs/index.html

serv:
	(cd ../../../docs && http-server)	

