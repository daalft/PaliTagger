package wordlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class WordlistGenerator {

	private String lemmaFile = "c:/senereko.david/misc/ped_david_1.csv";
	
	public void run () throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(lemmaFile)));
		String l = "";
		while ((l=br.readLine())!= null) {
			String[] sp = l.split("\t");
			// 0: id
			// 1: lemma
			String lemma = sp[1];
			// 2: pos / possibly more than 1
			String pos = sp[2];
			// 3: mfn
			String mfn = sp[3];
			// 4: form
			String form = sp[4];
			// 5: link
			String link = sp[5];
			// 6: comp
			String comp = sp[6];
			// 7: def
						
			if (pos.equals("verb")) {
				// ignore verbs for regular generation
				continue;
			}
			
			if (!form.isEmpty()) {
				if (!form.equals("ppp"))
					// don't use forms for generation except ppp as adj
					continue;
			}
			
			if (!link.isEmpty()) {
				// ignore links, the linked form will be used for generation
				continue;
			}
			

			// shortcut indecl
			if (pos.equals("indeclinable")) {
				// TODO treat
				continue;
			}
			
		}


		// ignore if is morph except ppp?
		br.close();
	}
	
	public static void main(String[] args) {
		try {
			new WordlistGenerator().run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
