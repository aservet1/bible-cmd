OBJS = src/bible_main.o \
       src/bible_match.o \
       src/bible_ref.o \
       src/bible_render.o \
       src/intset.o \
       src/strutil.o \
       data/bible_data.o
CFLAGS += -Wall -Isrc/
LDLIBS += -lreadline

bible: $(OBJS)
	$(CC) -o $@ $(LDFLAGS) $(OBJS) $(LDLIBS)

src/bible_main.o: src/bible_main.c src/bible_config.h src/bible_data.h src/bible_match.h src/bible_ref.h src/bible_render.h src/strutil.h

src/bible_match.o: src/bible_match.h src/bible_match.c src/bible_config.h src/bible_data.h src/bible_ref.h

src/bible_ref.o: src/bible_ref.h src/bible_ref.c src/intset.h src/bible_data.h

src/bible_render.o: src/bible_render.h src/bible_render.c src/bible_config.h src/bible_data.h src/bible_match.h src/bible_ref.h

src/insetset.o: src/intset.h src/insetset.c

src/strutil.o: src/strutil.h src/strutil.c

data/bible_data.o: src/bible_data.h data/bible_data.c

data/bible_data.c: data/kjv.tsv data/generate.awk src/bible_data.h
	awk -f data/generate.awk $< > $@

.PHONY: clean
clean:
	rm -rf $(OBJS) bible
