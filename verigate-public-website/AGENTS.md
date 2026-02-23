# Repository Guidelines

## Project Structure & Module Organization
The Vite + React codebase lives in `src/`. Route-level views stay in `src/pages`, shared building blocks in `src/components` (design system primitives reside in `src/components/ui`), hooks in `src/hooks`, and cross-cutting helpers in `src/lib`. Images and media belong to `src/assets`. Static files exposed verbatim ship from `public/`. Reference material and marketing content sit in `docs/`. Production bundles land in `dist/`; regenerate them with the build scripts rather than editing by hand.

## Build, Test, and Development Commands
Run `npm install` once per clone. `npm run dev` starts the Vite dev server with HMR. `npm run build` creates an optimized bundle in `dist/`, while `npm run build:dev` compiles with development flags to debug bundling issues. Use `npm run preview` to smoke-test the production build locally. Enforce lint rules with `npm run lint`.

## Coding Style & Naming Conventions
Author components as typed function components in `.tsx`. Use `PascalCase` for component files, `camelCase` for helpers, and prefix custom hooks with `use`. Imports should group external modules before local paths, leveraging the `@/` alias for anything under `src`. Follow the prevailing 2-space indentation and favor Tailwind utility classes over ad-hoc CSS; extend shared styles through `tailwind.config.ts` when repeated. Keep comments purposeful and rely on props and naming for clarity.

## Testing Guidelines
Automated tests are not yet configured. When adding coverage, introduce `vitest` plus `@testing-library/react`, colocating specs as `<filename>.test.tsx` beside the module. Target critical flows such as navbar routing, hero CTAs, and form validation. For now, validate builds by running `npm run build` followed by `npm run preview` to exercise the generated output in a browser session.

## Commit & Pull Request Guidelines
Existing history mixes imperative summaries ("Add Pricing Page...") with conventional prefixes (`chore:`). Prefer `type: imperative summary` such as `feat: add onboarding hero`, keeping the first line under 72 characters. Reference issues with `#id` in the body when closing work. Pull requests should state the problem, outline the solution, and include screenshots or GIFs for UI changes plus a checklist of executed commands (dev, build, lint). Keep PRs focused; note follow-ups explicitly instead of bundling unrelated work.

## Configuration Notes
Environment variables follow Vite conventions: define `VITE_`-prefixed keys in `.env.local` (git-ignored). Tailwind design tokens live in `tailwind.config.ts`; update `components.json` when scaffolding new shadcn UI primitives so tokens stay in sync. Shared constants or API adapters belong in `src/lib` to avoid duplicating logic across pages.
