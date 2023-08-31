<?php
/**
 * This is project's console commands configuration for Robo task runner.
 *
 * @see https://robo.li/
 */
class RoboFile extends \Robo\Tasks
{
    public function buildLog($a, $b = 'HEAD', $with_head = true) {
        $log = ConventionalChangelog::buildLog($a, $b, $with_head);
        echo implode(PHP_EOL, $log);
    }

}

class ConventionalChangelog
{
   /**
    * @param array $commmits commits to filter
    */
   public static function filterCommits($commits) {
      $types = [
         'build', 'chore', 'ci', 'docs', 'fix', 'feat', 'perf', 'refactor', 'style', 'test'
      ];
      $types = implode('|', $types);
      $scope = "(\((?P<scope>[^\)]*)\))?";
      $subject = ".*";
      $filter = "/^(?P<type>$types)$scope:(?P<subject>$subject)$/";
      $filtered = [];
      $matches = null;
      foreach ($commits as $commit) {
         if (preg_match($filter, $commit->subject, $matches) === 1) {
            $commit->type = $matches['type'];
            $commit->scope = '';
            if (isset($matches['scope']) && strlen($matches['scope']) > 0) {
               $commit->scope = $matches['scope'];
            }
            $commit->subject = trim($matches['subject']);
            $filtered[] = $commit;
         }
      }

      return $filtered;
   }

   public static function compareCommits($a, $b) {
      // comapre scopes
      if ($a->scope < $b->scope) {
         return -1;
      } else if ($a->scope > $b->scope) {
         return 1;
      }

      // then compare subject
      if ($a->subject < $b->subject) {
         return -1;
      } else if ($a->subject > $b->subject) {
         return 1;
      }

      return 0;
   }


   public static function buildLog($a, $b = 'HEAD', $with_head = true) {
      // if (!Git::tagExists($b)) {
      //    // $b is not a tag, try to find a matching one
      //    if (Git::getTagOfCommit($b) === false) {
      //       throw new Exception("current HEAD does not match a tag");
      //    }
      // }

      // get All tags between $a and $b
      $tags = Git::getAllTags();

      // Remove non semver compliant versions
      $tags = array_filter($tags, function ($tag) {
         return Semver::isSemVer($tag);
      });



      $startVersion = $a;
      $endVersion = $b;
      $prefix = 'v';
      if (substr($startVersion, 0, strlen($prefix)) == $prefix) {
         $startVersion = substr($startVersion, strlen($prefix));
      }
      if (substr($endVersion, 0, strlen($prefix)) == $prefix) {
         $endVersion = substr($endVersion, strlen($prefix));
      }


      $tags = array_filter($tags, function ($version) use ($startVersion, $endVersion) {
         $prefix = 'v';
         if (substr($version, 0, strlen($prefix)) == $prefix) {
            $version = substr($version, strlen($prefix));
         }
         if (version_compare($version, $startVersion) < 0) {
            return false;
         }
         if ($endVersion !== 'HEAD' && version_compare($version, $endVersion) > 0) {
            return false;
         }
         return true;
      });

      // sort tags older DESCENDING
      usort($tags, function ($a, $b) {
         return version_compare($b, $a);
      });

      $log = [];
      if ($b === 'HEAD') {
         array_unshift($tags, $b);
      }
      $startRef = array_shift($tags);
      if ($startRef === null) {
         throw new RuntimeException("$a not found");
      }
      while ($endRef = array_shift($tags)) {
         $log = array_merge($log, self::buildLogOneBump($startRef, $endRef, $with_head));
         $startRef = $endRef;
      }

      if ($with_head) {
         $log = array_merge($log, self::buildLogOneBump($startRef, null , $with_head));
      }

      return $log;
   }

   public static function buildLogOneBump($a, $b, $with_head = true) {
      $tag = $a;
      /*if (!Git::tagExists($b)) {
         // $b is not a tag, try to find a matching one
         if ($tag = Git::getTagOfCommit($b) === false) {
            $tag = 'Unreleased';
         }
      }*/

      // get remote
      $remotes = Git::getRemotes();
      $remote = $remotes['origin'];

      // Get all commits from A to B
      $commits = Git::createCommitList(Git::getLog($b, $a));

      // Remove non conventional commits
      $commits = self::filterCommits($commits);

      // Keep only useful commits for changelog
      $fixes = array_filter($commits, function ($commit) {
         if (in_array($commit->type, ['fix'])) {
            return true;
         }
         return false;
      });
      $feats = array_filter($commits, function ($commit) {
         if (in_array($commit->type, ['feat'])) {
            return true;
         }
         return false;
      });

      // Sort commits
      usort($fixes, [self::class, 'compareCommits']);
      usort($feats, [self::class, 'compareCommits']);

      // generate markdown log
      $log = [];

      $tagDate = (new DateTime())->format('Y-m-d');
      $compare = "$remote/compare/$b..";
      if ($tag !== 'Unreleased') {
         $tagDate = Git::getTagDate($tag)->format('Y-m-d');
         $compare .= $tag;
      } else {
         $compare .= Git::getCurrentBranch();
      }

      if ($with_head && $tag !== 'HEAD') {
        $log[] = '<a name="' . $tag . '"></a>';
        $log[] = '## [' . $tag . '](' . $compare . ') (' . $tagDate . ')';
      }


      if (count($fixes) > 0) {

        if ($with_head) {
            $log[] = '';
            $log[] = '';
        }
         $log[] = '### Bug Fixes';
         $log[] = '';
         foreach ($fixes as $commit) {
            $log[]  = self::buildLogLine($commit, $remote);
         }
      }

      if (count($feats) > 0) {
         $log[] = '';
         $log[] = '';
         $log[] = '### Features';
         $log[] = '';
         foreach ($feats as $commit) {
            $log[]  = self::buildLogLine($commit, $remote);
         }
      }

      $log[] = '';
      $log[] = '';
      $log[] = '';

      return $log;
   }

   public static function buildLogLine($commit, $remote) {
      $line = '* ';
      $scope = $commit->scope;
      if ($scope !== '') {
         $scope = "**$scope:**";
         $line .= $scope;
      }
      $hash = $commit->hash;
      $line .= " $commit->subject"
      . " ([$hash]($remote/commit/$hash))";

      // Search for closed issues
      $body = explode(PHP_EOL, $commit->body ?? '');
      $pattern = '/^((close|closes|fix|fixed) #(?P<id>\\d+)(,\s+)?)/i';
      $commit->close = [];
      foreach ($body as $bodyLine) {
         $matches = null;
         if (preg_match($pattern, $bodyLine, $matches)) {
            if (!is_array($matches['id'])) {
               $matches['id'] = [$matches['id']];
            }
            $commit->close = $matches['id'];
         }
      }
      if (count($commit->close) > 0) {
         foreach ($commit->close as &$issue) {
            $issue = "[#$issue]($remote/issues/$issue)";
         }
         $line .= ', closes ' . implode(', ', $commit->close);

      }

      return $line;
   }
}

class Git
{

   public static function getCurrentCommitHash() {
      exec('git rev-parse HEAD', $output, $retCode);
      if ($retCode != '0') {
         throw new Exception("failed to get curent commit hash");
      }
      return $output[0];
   }

   public static function isTagMatchesCurrentCommit($tag) {
      $commitHash = self::getCurrentCommitHash();
      exec("git tag -l --contains $commitHash", $output, $retCode);
      if (isset($output[0]) && $output[0] == $tag) {
         return  true;
      }

      return false;
   }

   public static function getTagOfCommit($hash) {
      exec("git tag -l --contains $hash", $output, $retCode);
      if (isset($output[0])) {
         return $output[0];
      }

      return false;
   }

   public static function getTagDate($tag) {
      exec("git log -1 --format=%ai $tag", $output, $retCode);
      if ($retCode != '0') {
         throw new Exception("failed to get date of a tag");
      }
      if (isset($output[0])) {
         return new DateTime($output[0]);
      }

      return false;
   }

   /**
    * get highest sember tag from list
    * @param array $tags list of tags
    */
   public static function getLastTag($tags, $prefix = '') {
      // Remove prefix from all tags
      if ($prefix !== '') {
         $newTags = [];
         foreach ($tags as $tag) {
            if (substr($tag, 0, strlen($prefix)) == $prefix) {
               $tag = substr($tag, strlen($prefix));
            }
            $newTags[] = $tag;
         }
         $tags = $newTags;
      }

      // get tags and keey only sember compatible ones
      $tags = array_filter($tags, [SemVer::class, 'isSemVer']);

      // Sort tags
      usort($tags, function ($a, $b) {
         return version_compare($a, $b);
      });

      return end($tags);
   }

   public static function createCommitList($commits) {
      // Biuld list of commits, latest is oldest
      $commitObjects = [];
      foreach ($commits as $commit) {
         $line = explode(' ', $commit, 2);
         $commitObject = new StdClass();
         $commitObject->hash = $line[0];
         $commitObject->subject = $line[1];
         $commitObjects[] = $commitObject;
         $commitObject->body = self::getCommitBody($commitObject->hash);
      }

      return $commitObjects;
   }

   public static function getLog($a, $b = 'HEAD') {
      if ($a === null) {
         exec("git log --oneline $b", $output, $retCode);
         if ($retCode != '0') {
            // An error occured
            throw new Exception("Unable to get log from the repository");
         }
      } else {
         exec("git log --oneline $a..$b", $output, $retCode);
         if ($retCode != '0') {
            // An error occured
            throw new Exception("Unable to get log from the repository");
         }
      }

      return $output;
   }

   public static function getRemotes() {
      exec("git remote -v", $output, $retCode);
      if ($retCode != '0') {
         // An error occured
         throw new Exception("Unable to get remotes of the repository");
      }
      $remotes = [];
      foreach ($output as $line) {
         $line = explode("\t", $line);
         $line[1] = explode(' ', $line[1]);
         $line[1] = $line[1][0];
         // 0 = name
         // 1 = URL
         // 2 = (fetch) or (push)
         if (strpos($line[1], 'git@') === 0) {
            // remote type is SSH
            $split = explode('@', $line[1]);
            //$user = $split[0];
            $split = explode(':', $split[1]);
            $url = 'https://' . $split[0] . '/' . $split[1];
         }

         if (strpos($line[1], 'https://') === 0) {
            $url = $line[1];
         }
         $remotes[$line[0]] = $url;
      }

      return $remotes;
   }


   public static function getAllTags() {
      exec("git tag -l", $output, $retCode);
      if ($retCode != '0') {
         // An error occured
         throw new Exception("Unable to get tags from the repository");
      }
      return $output;
   }

   public static function tagExists($version) {
      $tags = self::getAllTags();
      return in_array($version, $tags);
   }

      /**
    * Get a file from git tree
    * @param string $path
    * @param string $rev a commit hash, a tag or a branch
    * @throws Exception
    * @return string content of the file
    */
   public static function getFileFromGit($path, $rev) {
      $output = shell_exec("git show $rev:$path");
      if ($output === null) {
         throw new Exception ("coult not get file from git: $rev:$path");
      }
      return $output;
   }

   public static function getCommitBody($hash) {
      $output = shell_exec("git log $hash --max-count=1 --pretty=format:\"%b\"");
      return $output;
   }

   public static function getCurrentBranch() {
      $output = shell_exec("git rev-parse --abbrev-ref HEAD");
      if ($output === null) {
         throw new Exception ("could not get current branch");
      }
      return $output;

   }
}
class SemVer
{
      /**
    * Check the version is made of numbers separated by dots
    *
    * Returns true if the version is well formed, false otherwise
    *
    * @param string $version
    * @return boolean
    */
   public static function isSemVer($version) {
      $semverPattern = '#\bv?(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)\.(?:0|[1-9]\d*)(?:-[\da-z\-]+(?:\.[\da-z\-]+)*)?(?:\+[\da-z\-]+(?:\.[\da-z\-]+)*)?\b#i';
      if (preg_match($semverPattern, $version) !== 1) {
         return false;
      }

      return true;
   }
}
