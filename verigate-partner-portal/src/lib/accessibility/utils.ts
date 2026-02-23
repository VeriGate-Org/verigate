// Accessibility utilities and helpers

// Focus management
export function trapFocus(element: HTMLElement) {
  const focusableElements = element.querySelectorAll<HTMLElement>(
    'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
  );
  
  const firstFocusable = focusableElements[0];
  const lastFocusable = focusableElements[focusableElements.length - 1];

  const handleTabKey = (e: KeyboardEvent) => {
    if (e.key !== "Tab") return;

    if (e.shiftKey) {
      if (document.activeElement === firstFocusable) {
        lastFocusable?.focus();
        e.preventDefault();
      }
    } else {
      if (document.activeElement === lastFocusable) {
        firstFocusable?.focus();
        e.preventDefault();
      }
    }
  };

  element.addEventListener("keydown", handleTabKey);

  return () => {
    element.removeEventListener("keydown", handleTabKey);
  };
}

// Announce to screen readers
export function announce(message: string, priority: "polite" | "assertive" = "polite") {
  const announcement = document.createElement("div");
  announcement.setAttribute("role", "status");
  announcement.setAttribute("aria-live", priority);
  announcement.setAttribute("aria-atomic", "true");
  announcement.className = "sr-only";
  announcement.textContent = message;

  document.body.appendChild(announcement);

  setTimeout(() => {
    document.body.removeChild(announcement);
  }, 1000);
}

// Check if user prefers reduced motion
export function prefersReducedMotion(): boolean {
  if (typeof window === "undefined") return false;
  return window.matchMedia("(prefers-reduced-motion: reduce)").matches;
}

// Check if user prefers high contrast
export function prefersHighContrast(): boolean {
  if (typeof window === "undefined") return false;
  return window.matchMedia("(prefers-contrast: high)").matches;
}

// Generate unique ID for ARIA attributes
let idCounter = 0;
export function generateId(prefix: string = "id"): string {
  return `${prefix}-${++idCounter}`;
}

// Keyboard navigation helpers
export const Keys = {
  ENTER: "Enter",
  SPACE: " ",
  ESCAPE: "Escape",
  ARROW_UP: "ArrowUp",
  ARROW_DOWN: "ArrowDown",
  ARROW_LEFT: "ArrowLeft",
  ARROW_RIGHT: "ArrowRight",
  TAB: "Tab",
  HOME: "Home",
  END: "End",
  PAGE_UP: "PageUp",
  PAGE_DOWN: "PageDown",
} as const;

// Handle keyboard navigation for lists
export function handleListNavigation(
  event: KeyboardEvent,
  items: HTMLElement[],
  currentIndex: number,
  options: {
    onSelect?: (index: number) => void;
    orientation?: "vertical" | "horizontal";
    loop?: boolean;
  } = {}
): number {
  const { onSelect, orientation = "vertical", loop = true } = options;
  let newIndex = currentIndex;

  const nextKey = orientation === "vertical" ? Keys.ARROW_DOWN : Keys.ARROW_RIGHT;
  const prevKey = orientation === "vertical" ? Keys.ARROW_UP : Keys.ARROW_LEFT;

  switch (event.key) {
    case nextKey:
      newIndex = currentIndex + 1;
      if (newIndex >= items.length) {
        newIndex = loop ? 0 : items.length - 1;
      }
      event.preventDefault();
      break;

    case prevKey:
      newIndex = currentIndex - 1;
      if (newIndex < 0) {
        newIndex = loop ? items.length - 1 : 0;
      }
      event.preventDefault();
      break;

    case Keys.HOME:
      newIndex = 0;
      event.preventDefault();
      break;

    case Keys.END:
      newIndex = items.length - 1;
      event.preventDefault();
      break;

    case Keys.ENTER:
    case Keys.SPACE:
      onSelect?.(currentIndex);
      event.preventDefault();
      break;
  }

  if (newIndex !== currentIndex) {
    items[newIndex]?.focus();
  }

  return newIndex;
}

// ARIA live region manager
class LiveRegionManager {
  private regions: Map<string, HTMLElement> = new Map();

  private ensureRegion(priority: "polite" | "assertive"): HTMLElement {
    const existing = this.regions.get(priority);
    if (existing) return existing;

    const region = document.createElement("div");
    region.setAttribute("role", "status");
    region.setAttribute("aria-live", priority);
    region.setAttribute("aria-atomic", "true");
    region.className = "sr-only";
    document.body.appendChild(region);

    this.regions.set(priority, region);
    return region;
  }

  announce(message: string, priority: "polite" | "assertive" = "polite") {
    const region = this.ensureRegion(priority);
    region.textContent = message;

    // Clear after announcement
    setTimeout(() => {
      region.textContent = "";
    }, 1000);
  }

  cleanup() {
    this.regions.forEach(region => {
      document.body.removeChild(region);
    });
    this.regions.clear();
  }
}

export const liveRegionManager = new LiveRegionManager();

// Focus visible utility (for keyboard-only focus indicators)
export function initFocusVisible() {
  if (typeof document === "undefined") return;

  let hadKeyboardEvent = false;

  const handleKeyDown = (e: KeyboardEvent) => {
    if (e.key === "Tab") {
      hadKeyboardEvent = true;
    }
  };

  const handleMouseDown = () => {
    hadKeyboardEvent = false;
  };

  const handleFocus = () => {
    if (hadKeyboardEvent) {
      document.body.classList.add("keyboard-navigation");
    } else {
      document.body.classList.remove("keyboard-navigation");
    }
  };

  document.addEventListener("keydown", handleKeyDown);
  document.addEventListener("mousedown", handleMouseDown);
  document.addEventListener("focusin", handleFocus);

  return () => {
    document.removeEventListener("keydown", handleKeyDown);
    document.removeEventListener("mousedown", handleMouseDown);
    document.removeEventListener("focusin", handleFocus);
  };
}

// Skip to content link helper
export function createSkipLink(targetId: string, label: string = "Skip to main content") {
  const link = document.createElement("a");
  link.href = `#${targetId}`;
  link.textContent = label;
  link.className = "skip-link";
  
  link.addEventListener("click", (e) => {
    e.preventDefault();
    const target = document.getElementById(targetId);
    if (target) {
      target.focus();
      target.scrollIntoView();
    }
  });

  return link;
}
