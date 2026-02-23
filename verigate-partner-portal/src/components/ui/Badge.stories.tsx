import type { Meta, StoryObj } from "@storybook/react";
import { Badge } from "./Badge";

const meta = {
  title: "UI/Badge",
  component: Badge,
  args: { children: "Badge" },
} satisfies Meta<typeof Badge>;
export default meta;

type Story = StoryObj<typeof meta>;

export const Neutral: Story = { args: { variant: "neutral" } };
export const Success: Story = { args: { variant: "success", children: "success" } };
export const Warning: Story = { args: { variant: "warning", children: "soft_fail" } };
export const Danger: Story = { args: { variant: "danger", children: "hard_fail" } };
