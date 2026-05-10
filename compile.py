import os
import subprocess

sources = []
for root, dirs, files in os.walk("src"):
    for f in files:
        if f.endswith(".java"):
            sources.append(os.path.join(root, f))

with open("sources.txt", "w") as f:
    f.write("\n".join(sources))

os.makedirs("bin", exist_ok=True)
res = subprocess.run(["javac", "-d", "bin", "@sources.txt"], capture_output=True, text=True)
print(res.stdout)
print(res.stderr)

if res.returncode == 0:
    print("SUCCESSFUL COMPILE!")
else:
    print("COMPILE FAILED!")
