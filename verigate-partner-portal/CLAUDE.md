# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

VeriGate Partner Portal is a Next.js 15 application for managing verification workflows and providers. It provides a partner interface to configure verification policies, review results, and manage provider adapters. The app handles various verification types including ID, AVS, SANCTIONS, CIPC, DEEDS, CREDIT, TAX, and DOCUMENT verification.

## Development Commands

- `npm run dev` - Start development server with Turbopack
- `npm run build` - Build production app with Turbopack  
- `npm start` - Start production server
- `npm run lint` - Run ESLint

## Architecture Overview

### Core Structure
- **Next.js App Router**: Uses the `src/app` directory structure
- **API Routes**: RESTful endpoints in `src/app/api/` for verifications, policies, adapters, and auth
- **Client Components**: Interactive components suffixed with `.client.tsx`
- **Server Components**: Default for pages and layouts
- **TypeScript**: Strict mode enabled with path aliases (`@/*` maps to `./src/*`)

### Key Domains

**Verification Management** (`src/app/verifications/`):
- View and filter verification jobs by status (in_progress, success, soft_fail, hard_fail)
- Table-based interface with client-side filtering
- Individual verification details with event timelines

**Policy Configuration** (`src/app/policies/`):
- Visual workflow builder using XYFlow/React Flow
- Drag-and-drop interface for creating verification sequences
- Policy validation and publishing capabilities
- Node inspector for detailed configuration

**Provider Adapters** (`src/app/adapters/`):
- Configure external verification providers
- Preference management for provider selection
- Provider-specific settings and credentials

**Audit Logging** (`src/app/audit/`):
- System audit trail and monitoring
- Event tracking and compliance reporting

### Core Types (`src/lib/types.ts`)

- `VerificationType`: ID, AVS, SANCTIONS, CIPC, DEEDS, CREDIT, TAX, DOCUMENT
- `VerificationStatus`: in_progress, success, soft_fail, hard_fail  
- `Verification`: Main verification entity with correlation tracking
- `VerificationEvent`: Event sourcing for audit trails
- `PartnerPolicySnapshot`: Workflow configuration schema

### Technology Stack

- **Frontend**: Next.js 15, React 19, TypeScript, Tailwind CSS
- **UI Components**: Radix UI primitives, Lucide icons, Framer Motion
- **Data Handling**: TanStack Query for API state, React Hook Form, Zod validation
- **Flow Builder**: XYFlow React for visual workflow creation
- **Testing**: Jest, Testing Library, MSW for API mocking
- **Auth**: NextAuth.js integration

### File Naming Conventions

- `.client.tsx`: Client components requiring browser APIs
- `page.tsx`: App Router page components  
- `layout.tsx`: Layout components
- `route.ts`: API route handlers
- `.types.ts`: TypeScript type definitions

### API Patterns

All API routes follow RESTful conventions:
- `GET /api/verifications` - List verifications with filtering
- `GET /api/verifications/[correlationId]` - Get verification details
- `GET /api/policies` - Get current policy configuration
- `POST /api/policies/validate` - Validate policy configuration
- `POST /api/policies/publish` - Publish policy changes

### Key Features

- **Real-time Status**: Verification status tracking with event timelines
- **Visual Policy Builder**: Drag-and-drop workflow configuration
- **Provider Management**: Multi-provider adapter configuration
- **Audit Trail**: Comprehensive event logging and compliance reporting
- **Responsive Design**: Mobile-first Tailwind CSS implementation

### Mock Data Structure

The application uses `src/lib/mock-db.ts` for development data including sample verifications, policies, and events. This provides realistic data for testing UI components and workflows.