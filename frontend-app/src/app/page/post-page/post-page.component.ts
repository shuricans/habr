import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-post-page',
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.scss']
})
export class PostPageComponent  {

  ngxMarkdownVersion = '14.0.1';

  markdown = `## Markdown __rulez__!
---
<img src="https://www.cloudsavvyit.com/p/uploads/2022/03/f6ad612a.jpg" alt="drawing" width="100%"/>

---

### Syntax highlight
\`\`\`typescript
const language = 'typescript';
\`\`\`

### Lists
1. Ordered list
2. Another bullet point
   - Unordered list
   - Another unordered bullet

### Blockquote
> Blockquote to the max`;
}
