# cmdline-java
A getopt/argparser implementation in java. Very similar to [getopt.tcl](https://github.com/tcler/getopt.tcl)

features in my cmdline-java:
1. generate usage/help info from option list.
2. support GNU style option and more flexible: -a --along --b -c carg -d=darg -ooptionalarg -- --notoption
2. not just support a short and a long option, you can define a *List* {h help Help ? 帮助}
3. hide attribute of option object， used to hide some option in usage/help info
4. forward option

test
```
git clone https://github.com/tcler/cmdline-java
cd cmdline-java
javac com/github/tcler/cmdline/*.java
java com.github.tcler.cmdline.Cmdline -h -H -f file --file file2 -e 's/abc/xyz/'  -r -n  -s=A -oa=b -S ''  -i -x xfile --wenj=file3 --www -aa -vvv -S DD -- -0 -y

# or:
javac com/github/tcler/cmdline/*.java
jar cfe cmdline.jar com.github.tcler.cmdline.Cmdline com/github/tcler/cmdline/*.class
java -jar cmdline.jar  -h -H -f file --file file2 -e 's/abc/xyz/'  -r -n  -s=A -oa=b -S ''  -i -x xfile --wenj=file3 --www -aa -vvv -S DD -- -0 -y
```

# Example code
see: 

