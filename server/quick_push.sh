#! /bin/bash
set -e

# Script to samplify the commit and push steps. All the arguments are concatenated as the commmit message.
cd "$(dirname $0)"
git pull

git add -A :/
git status
if [ $# -eq 0 ]; then
	echo "Warning: Better to leave a message to this commit. Using default message: update"
	echo ""
	git commit -m "Update"
else
	git commit -m "$*"
fi
git push


# Other tips
# 1 ssh login https://help.github.com/articles/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent/ then run: ssh-keyscan -t rsa github.com >> ~/.ssh/known_hosts

