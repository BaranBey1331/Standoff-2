import math

class Vector3:
    def __init__(self, x, y, z):
        self.x = x
        self.y = y
        self.z = z

def world_to_screen(pos, view_matrix, screen_w, screen_h):
    clip_x = pos.x * view_matrix[0] + pos.y * view_matrix[1] + pos.z * view_matrix[2] + view_matrix[3]
    clip_y = pos.x * view_matrix[4] + pos.y * view_matrix[5] + pos.z * view_matrix[6] + view_matrix[7]
    clip_w = pos.x * view_matrix[12] + pos.y * view_matrix[13] + pos.z * view_matrix[14] + view_matrix[15]

    if clip_w < 0.1:
        return None

    ndc_x = clip_x / clip_w
    ndc_y = clip_y / clip_w

    screen_x = (screen_w / 2 * ndc_x) + (ndc_x + screen_w / 2)
    screen_y = -(screen_h / 2 * ndc_y) + (ndc_y + screen_h / 2)

    return (screen_x, screen_y)

def calculate_aim_angles(local_pos, enemy_pos):
    dx = enemy_pos.x - local_pos.x
    dy = enemy_pos.y - local_pos.y
    dz = enemy_pos.z - local_pos.z

    distance_2d = math.sqrt(dx**2 + dy**2)
    yaw = math.atan2(dy, dx) * 180 / math.pi
    pitch = -math.atan2(dz, distance_2d) * 180 / math.pi

    return yaw, pitch

def smooth_angle(current_angle, target_angle, smooth_factor):
    return current_angle + (target_angle - current_angle) / smooth_factor
