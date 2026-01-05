// =================== BCSgit.js ===================
// Inline Git checkpoint support for BCS
// ------------------------------------
// Usage in BookMarkdown:
//
//   ::G v1-hello
//
// This renders a Git checkpoint block at the
// exact location where ::G appears.
//
// Assumptions:
//   - GitHub repo uses tags for checkpoints
//   - Repo is public
//   - BCS.js is loaded first
// ================================================

// -------- Configuration --------
var GitConfig = {
  owner: "Marlin314",  
  repo:  "GitNotesTest",  
  branch: "main"
};

// -------- Helpers --------
function gitRepoUrl() {
  return `https://github.com/${GitConfig.owner}/${GitConfig.repo}`;
}

function gitTreeUrl(tag) {
  return `${gitRepoUrl()}/tree/${tag}`;
}

// -------- Markdown Directive --------
function gitChunk(chunk) {
  // chunk is "::G <tag>"
  var tag = chunk.substring(4).trim();

  if (!tag) {
    alert("BCSgit: ::G requires a git tag");
    return;
  }

  sectionContents += `
    <div class="GitCheckpoint">
      <p>
        <b>Git Checkpoint:</b> ${tag}<br/>
        <a href="${gitTreeUrl(tag)}" target="_blank">
          View code snapshot on GitHub
        </a>
      </p>
      <pre>git fetch --tags
git checkout ${tag}</pre>
    </div>
  `;
}

// -------- Hook into BCS --------

// Save original bookChunk
var _bcs_git_origBookChunk = bookChunk;

// Replace bookChunk with extended version
bookChunk = function (chunk) {
  var type = chunk.substring(0, 4);

  if (type === "::G ") {
    gitChunk(chunk);
  } else {
    _bcs_git_origBookChunk(chunk);
  }
};
