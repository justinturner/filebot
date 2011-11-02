// filebot -script "http://filebot.sourceforge.net/data/shell/rsam.groovy" <options> <folder>

import net.sourceforge.filebot.similarity.*

def isMatch(a, b) { new NameSimilarityMetric().getSimilarity(a, b) > 0.9 }

/*
 * Rename anime, tv shows or movies (assuming each folder represents one item)
 */
args.eachMediaFolder { dir ->
	def n = dir.name
	def lang = Locale.ENGLISH
	
	[	[db:anidb,      query:{ anidb.search(n, lang).find{ isMatch(it, n) } }],
		[db:thetvdb,    query:{ thetvdb.search(n, lang).find{ isMatch(it, n) } }],
		[db:themoviedb, query:{ themoviedb.searchMovie(n, lang).find{ isMatch(it, n) } }]
	].find {
		def match = it.query()
		if (match) { rename(folder:dir, db:it.db.name, query:match.name) }
		return match
	}
}
