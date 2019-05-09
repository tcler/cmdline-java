import java.util.Arrays;
import java.util.ArrayList;
import com.github.tcler.cmdline.*;

class CmdlineTest {
	public static void main(String[] argv) {
		if (argv.length < 1) {
			System.out.println("argv is emplty, use test argv instead:");
			argv = new String[]{ "-h", "-H", "-f", "file", "--file", "file2", "-e", "s/abc/xyz/", "-r", "-n", "-s=A", "-oa=b", "-S", "", "-i", "-x", "xfile", "--wenj=file3", "--www", "-aa", "-vvv", "-S", "DD", "--", "-0", "-y" };
		}
		System.out.printf("argv: %s\n", Arrays.toString(argv));
		System.out.printf("----------------------------------------------------------------\n\n");
		ArrayList<Option> optionList = new ArrayList<Option>();
		optionList.add(new Option.Builder("Options group1:").build());
		optionList.add(new Option.Builder("help h H 帮助", "print this help").build());
		optionList.add(new Option.Builder("file f", "specify file", Option.ArgType.M).build());
		optionList.add(new Option.Builder("conf c", "config file", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("o", "mount option", Option.ArgType.O).build());
		optionList.add(new Option.Builder("v", "verbose output, -vvv means verbose level 3").build());
		optionList.add(new Option.Builder("x", "dump binary file to text", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("s", "enable smart mode", Option.ArgType.Y).build());
		optionList.add(new Option.Builder("S", "").link("s").hide().build());

		optionList.add(new Option.Builder("\nOptions group2:").build());
		optionList.add(new Option.Builder("e", "sed -e option, will forward to child sed process", Option.ArgType.M).forward().build());
		optionList.add(new Option.Builder("r", "sed -r option, will forward to child sed process", Option.ArgType.Y).forward().build());
		optionList.add(new Option.Builder("n", "sed -n option, will forward to child sed process", Option.ArgType.Y).forward().build());

		Cmdline cl = new Cmdline(optionList, argv);

		if (cl.hasOption("help")) {
			cl.getUsage(optionList);
		}
		if (cl.hasOption("file")) {
			System.out.println("opt(file): " + cl.getOptionArgList("file").toString());
		}
		if (cl.hasOption("conf")) {
			System.out.println("opt(conf): " + cl.getOptionArgString("conf"));
		}
		if (cl.hasOption("o")) {
			System.out.println("opt(o): " + cl.getOptionArgString("o"));
		}

		if (cl.hasOption("v")) {
			int verboselevel = cl.getOptionNumber("v");
			System.out.println("opt(v): " + verboselevel);
		}
		if (cl.hasOption("s")) {
			System.out.println("opt(s): " + cl.getOptionArgString("s"));
		}

		System.out.println("invalidOptions: " + cl.getInvalidOptions().toString());
		System.out.println("forwardOptions: " + cl.getForwardOptions().toString());
		System.out.println("args: " + cl.getArgs().toString());
	}
}
